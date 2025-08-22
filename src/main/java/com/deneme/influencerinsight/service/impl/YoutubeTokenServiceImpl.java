package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.YoutubeTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
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

    @Override
    public String ensureValidAccessToken(SocialMediaAccountEntity account) {
        if (account == null) {
            throw new IllegalArgumentException("Account cannot be null.");
        }

        Instant expiresAt = account.getTokenExpiresAt();
        if (expiresAt != null) {
            Instant safety = Instant.now().plusSeconds(60);
            if (expiresAt.isAfter(safety)) {
                String current = account.getAccessToken();
                if (current != null && !current.isBlank()) {
                    return current;
                }
            }
        }

        String refreshToken = account.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("Missing refresh token for YouTube account.");
        }

        Map<String, String> form = Map.of(
                "client_id", clientId,
                "client_secret", clientSecret,
                "refresh_token", refreshToken,
                "grant_type", "refresh_token"
        );

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String body = toFormData(form.keySet(), form);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        Map<String, Object> tokenBody = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        ).getBody();

        if (tokenBody == null || tokenBody.get("access_token") == null) {
            throw new RuntimeException("Failed to refresh access token.");
        }

        String newAccessToken = tokenBody.get("access_token").toString();
        Integer expiresInSeconds = tokenBody.get("expires_in") instanceof Integer
                ? (Integer) tokenBody.get("expires_in")
                : null;

        account.setAccessToken(newAccessToken);
        if (expiresInSeconds != null) {
            account.setTokenExpiresAt(Instant.now().plusSeconds(expiresInSeconds));
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
