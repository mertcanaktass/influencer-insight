package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.TiktokOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TiktokOAuthServiceImpl implements TiktokOAuthService {

    private final SocialMediaAccountRepository accountRepository;
    private final RestClient rest = RestClient.create();
    
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
    public SocialMediaAccountEntity exchangeCodeAndPersist(String appUsername, String stateIgnored, String code) {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("client_key", clientKey);
        form.add("client_secret", clientSecret);
        form.add("code", code);
        form.add("grant_type", "authorization_code");
        form.add("redirect_uri", redirectUri);

        Map<String, Object> resp = rest.post()
                .uri(baseUrl + "/v2/oauth/token/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if (resp == null || resp.isEmpty()) {
            throw new RuntimeException("Failed to exchange code for tokens.");
        }

        String accessToken = (String) resp.get("access_token");
        String refreshToken = (String) resp.get("refresh_token");
        Number expiresInSec = (Number) resp.getOrDefault("expires_in", 3600);
        String openId = (String) resp.get("open_id");

        SocialMediaAccountEntity account = accountRepository
                .findFirstByUsernameAndPlatform(appUsername, SocialMediaPlatform.TIKTOK)
                .orElseGet(() -> SocialMediaAccountEntity.builder()
                        .platform(SocialMediaPlatform.TIKTOK)
                        .username(appUsername)
                        .build());

        account.setAccessToken(accessToken);
        account.setRefreshToken(refreshToken);
        account.setTokenExpiresAt(Instant.now().plusSeconds(expiresInSec.longValue()));
        account.setExternalId(openId);

        return accountRepository.save(account);
    }
}
