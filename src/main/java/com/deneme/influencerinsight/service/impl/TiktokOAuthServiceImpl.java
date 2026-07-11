package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.security.SocialTokenCipher;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.service.TiktokOAuthService;
import com.deneme.influencerinsight.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class TiktokOAuthServiceImpl implements TiktokOAuthService {

    private final SocialMediaAccountRepository accountRepository;
    private final UserService userService;
    private final ExternalApiClient externalApiClient;
    private final SocialTokenCipher socialTokenCipher;
    
    @Value("${tiktok.client-key}")
    private String clientKey;
    
    @Value("${tiktok.client-secret}")
    private String clientSecret;
    
    @Value("${tiktok.base-url}")
    private String baseUrl;
    
    @Value("${tiktok.auth-base-url}")
    private String authBaseUrl;
    
    @Value("${tiktok.scopes}")
    private String scopes;
    
    @Value("${tiktok.redirect-uri}")
    private String redirectUri;

    public URI buildAuthorizationUri(String state) {
        return UriComponentsBuilder
                .fromUriString(authBaseUrl + "/v2/auth/authorize/")
                .queryParam("client_key", clientKey)
                .queryParam("scope", scopes)
                .queryParam("response_type", "code")
                .queryParam("redirect_uri", redirectUri)
                .queryParam("state", state)
                .build(true)
                .toUri();
    }

    @Transactional
    public SocialMediaAccountEntity exchangeCodeAndPersist(String username, String code) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Missing username (state).");
        }

        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_key", clientKey);
        form.add("client_secret", clientSecret);
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", redirectUri);

        Map<String, Object> resp = externalApiClient.postForm(
                "TikTok API",
                URI.create(baseUrl + "/v2/oauth/token/"),
                form,
                new ParameterizedTypeReference<>() {
                });

        if (resp == null || resp.isEmpty()) {
            throw new RuntimeException("Failed to exchange code for tokens.");
        }

        String accessToken = (String) resp.get("access_token");
        String refreshToken = (String) resp.get("refresh_token");
        long expiresInSeconds = getLong(resp.get("expires_in"), 3600);
        String openId = (String) resp.get("open_id");

        // user_id NOT NULL olduğu için hesabı sahibine bağla; ayrıca lookup'ı
        // Instagram/YouTube ile aynı anahtara (user.username + platform) getir.
        UserEntity user = userService.getRequiredUserByUsername(username);

        SocialMediaAccountEntity account = accountRepository
                .findAllByUserUsernameAndPlatform(username, SocialMediaPlatform.TIKTOK)
                .stream().findFirst()
                .orElseGet(() -> SocialMediaAccountEntity.builder()
                        .platform(SocialMediaPlatform.TIKTOK)
                        .user(user)
                        .username(username)
                        .build());

        account.setAccessToken(socialTokenCipher.encrypt(accessToken));
        account.setRefreshToken(socialTokenCipher.encrypt(refreshToken));
        account.setTokenExpiresAt(Instant.now().plusSeconds(expiresInSeconds));
        account.setExternalId(openId);

        return accountRepository.save(account);
    }

    private long getLong(Object value, long defaultValue) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        if (value instanceof String stringValue && !stringValue.isBlank()) {
            try {
                return Long.parseLong(stringValue);
            } catch (NumberFormatException ignored) {
                return defaultValue;
            }
        }
        return defaultValue;
    }
}
