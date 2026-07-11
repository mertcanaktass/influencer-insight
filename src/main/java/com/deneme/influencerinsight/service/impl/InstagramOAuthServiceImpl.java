package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.security.SocialTokenCipher;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.service.InstagramOAuthService;
import com.deneme.influencerinsight.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.deneme.influencerinsight.util.JsonUtils.*;

@Service
@RequiredArgsConstructor
public class InstagramOAuthServiceImpl implements InstagramOAuthService {

    @Value("${instagram.client-id}")
    private String clientId;
    @Value("${instagram.client-secret}")
    private String clientSecret;
    @Value("${instagram.redirect-uri}")
    private String redirectUri;
    @Value("${instagram.scopes}")
    private String scopesCsv;
    @Value("${instagram.graph-version:v21.0}")
    private String graphVer;
    @Value("${instagram.longlived.enabled:true}")
    private boolean longLivedEnabled;

    private final UserService userService;
    private final SocialMediaAccountRepository accountRepo;
    private final ExternalApiClient externalApiClient;
    private final SocialTokenCipher socialTokenCipher;

    @Override
    public String buildAuthorizationUrl(String state) {
        String scopes = Arrays.stream(scopesCsv.split(","))
                .map(String::trim)
                .collect(Collectors.joining(","));
        return UriComponentsBuilder.fromUriString("https://www.facebook.com/" + graphVer + "/dialog/oauth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", scopes)
                .queryParam("state", state)
                .build()
                .toUriString();
    }

    @Override
    public void exchangeCodeAndSaveAccount(String username, String code) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Missing username (state).");
        }

        Map<String, Object> userToken = externalApiClient.get(
                "Facebook Graph API",
                "https://graph.facebook.com/" + graphVer + "/oauth/access_token",
                Map.of("client_id", clientId, "client_secret", clientSecret, "redirect_uri", redirectUri, "code", code),
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        String userAccessToken = getString(userToken, "access_token");
        if (userAccessToken == null || userAccessToken.isBlank()) {
            throw new RuntimeException("Failed to obtain user access token.");
        }

        if (longLivedEnabled) {
            Map<String, Object> ll = externalApiClient.get(
                    "Facebook Graph API",
                    "https://graph.facebook.com/" + graphVer + "/oauth/access_token",
                    Map.of("grant_type", "fb_exchange_token", "client_id", clientId, "client_secret", clientSecret,
                            "fb_exchange_token", userAccessToken),
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            String llToken = getString(ll, "access_token");
            if (llToken != null && !llToken.isBlank()) {
                userAccessToken = llToken;
            }
        }

        Map<String, Object> accounts = externalApiClient.get(
                "Facebook Graph API",
                "https://graph.facebook.com/" + graphVer + "/me/accounts",
                Map.of("fields", "id,name,access_token,instagram_business_account"),
                userAccessToken,
                new ParameterizedTypeReference<>() {
                }
        );

        List<Map<String, Object>> data = toListOfMaps(accounts.get("data"));
        if (data.isEmpty()) {
            throw new RuntimeException("No Facebook Pages found on this account.");
        }

        Map<String, Object> pick = data.stream()
                .filter(m -> m.get("instagram_business_account") != null)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No linked Instagram Business account on any Page."));

        String pageAccessToken = getString(pick, "access_token");
        Map<String, Object> igObj = toMap(pick.get("instagram_business_account"));
        String igUserId = getString(igObj, "id");

        if (igUserId == null || pageAccessToken == null || pageAccessToken.isBlank()) {
            throw new RuntimeException("Missing instagram_business_account.id or page access token.");
        }

        Map<String, Object> igProfile = externalApiClient.get(
                "Facebook Graph API",
                "https://graph.facebook.com/" + graphVer + "/" + igUserId,
                Map.of("fields", "username,profile_picture_url,followers_count,media_count,name"),
                pageAccessToken,
                new ParameterizedTypeReference<>() {
                }
        );

        String igUsername = getStringOrDefault(igProfile, "username", username);
        String profileUrl = "https://www.instagram.com/" + igUsername;

        UserEntity user = userService.getRequiredUserByUsername(username);

        SocialMediaAccountEntity account = accountRepo
                .findAllByUserUsernameAndPlatform(username, SocialMediaPlatform.INSTAGRAM)
                .stream().findFirst()
                .orElseGet(() -> SocialMediaAccountEntity.builder()
                        .platform(SocialMediaPlatform.INSTAGRAM)
                        .user(user)
                        .build());

        account.setAccessToken(socialTokenCipher.encrypt(pageAccessToken));
        account.setExternalId(igUserId);
        account.setUsername(igUsername);
        account.setProfileUrl(profileUrl);
        account.setLastSyncedAt(Instant.now());
        account.setExtraData(toJson(igProfile));

        accountRepo.save(account);
    }

}
