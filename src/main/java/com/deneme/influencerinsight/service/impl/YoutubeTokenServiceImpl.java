package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.YoutubeTokenService;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.security.SocialTokenCipher;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class YoutubeTokenServiceImpl implements YoutubeTokenService {

    @Value("${google.youtube.client-id}")
    private String clientId;

    @Value("${google.youtube.client-secret}")
    private String clientSecret;

    private final SocialMediaAccountRepository accountRepository;
    private final ExternalApiClient externalApiClient;
    private final SocialTokenCipher socialTokenCipher;

    @Override
    public String ensureValidAccessToken(SocialMediaAccountEntity account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }

        Instant expiresAt = account.getTokenExpiresAt();
        if (expiresAt != null) {
            Instant safety = Instant.now().plusSeconds(60);
            if (expiresAt.isAfter(safety)) {
                String current = socialTokenCipher.decrypt(account.getAccessToken());
                if (current != null && !current.isBlank()) {
                    return current;
                }
            }
        }

        String refreshToken = socialTokenCipher.decrypt(account.getRefreshToken());
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("Missing refresh token for YouTube account.");
        }

        Map<String, String> form = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "refresh_token", refreshToken,
                "grant_type", "refresh_token"
        );

        Map<String, Object> tokenBody = externalApiClient.postForm(
                "Google OAuth",
                "https://oauth2.googleapis.com/token",
                toFormData(form.keySet(), form),
                new ParameterizedTypeReference<>() {
                }
        );

        if (tokenBody.get("access_token") == null) {
            throw new RuntimeException("Failed to refresh access token.");
        }

        String newAccessToken = String.valueOf(tokenBody.get("access_token"));

        Object expObj = tokenBody.get("expires_in");
        Integer expiresInSeconds = null;
        if (expObj instanceof Integer i) {
            expiresInSeconds = i;
        } else if (expObj instanceof String s && !s.isBlank()) {
            try {
                expiresInSeconds = Integer.parseInt(s);
            } catch (NumberFormatException ignored) { /* no-op */ }
        }

        account.setAccessToken(socialTokenCipher.encrypt(newAccessToken));

        Instant newExpiry = null;
        if (expiresInSeconds != null) {
            newExpiry = Instant.now().plusSeconds(expiresInSeconds);
        }
        if (newExpiry != null) {
            account.setTokenExpiresAt(newExpiry);
        }

        accountRepository.save(account);
        return newAccessToken;
    }

    private String toFormData(Set<String> keys, Map<String, String> map) {
        return keys.stream()
                .map(key -> UriUtils.encode(key, StandardCharsets.UTF_8) + "=" +
                        UriUtils.encode(map.get(key), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
