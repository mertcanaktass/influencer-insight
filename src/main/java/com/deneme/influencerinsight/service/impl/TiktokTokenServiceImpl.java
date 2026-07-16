package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.security.SocialTokenCipher;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.TiktokTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.net.URI;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TiktokTokenServiceImpl implements TiktokTokenService {

    private final SocialMediaAccountRepository accountRepository;
    private final ExternalApiClient externalApiClient;
    private final SocialTokenCipher socialTokenCipher;

    @Value("${tiktok.token-refresh-window-minutes:5}")
    private long tokenRefreshWindowMinutes;

    @Value("${tiktok.client-key}")
    private String clientKey;

    @Value("${tiktok.client-secret}")
    private String clientSecret;

    @Value("${tiktok.base-url}")
    private String baseUri;

    @Override
    public String ensureValidAccessToken(SocialMediaAccountEntity account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }

        Instant expiresAt = account.getTokenExpiresAt();
        Instant now = Instant.now();
        if (expiresAt != null && expiresAt.isAfter(now.plus(Duration.ofMinutes(tokenRefreshWindowMinutes)))) {
            String accessToken = socialTokenCipher.decrypt(account.getAccessToken());
            if (accessToken != null && !accessToken.isBlank()) {
                return accessToken;
            }
        }

        String refreshToken = socialTokenCipher.decrypt(account.getRefreshToken());
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("Missing refresh token for Tiktok account.");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_key", clientKey);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        form.add("grant_type", "refresh_token");


        Map<String, Object> resp = externalApiClient.postForm(
                "TikTok API",
                URI.create(baseUri + "/v2/oauth/token/"),
                form,
                new ParameterizedTypeReference<>() {
                });


        if (resp == null || resp.isEmpty()) {
            throw new RuntimeException("Failed to exchange code for tokens.");
        }

        String respAccessToken = (String) resp.get("access_token");
        String respRefreshToken = (String) resp.getOrDefault("refresh_token", refreshToken);
        long expiresInSeconds = getLong(resp.get("expires_in"), 3600);

        account.setAccessToken(socialTokenCipher.encrypt(respAccessToken));
        account.setRefreshToken(socialTokenCipher.encrypt(respRefreshToken));
        account.setTokenExpiresAt(Instant.now().plusSeconds(expiresInSeconds));

        accountRepository.save(account);
        return respAccessToken;
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
