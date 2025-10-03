package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.TiktokTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TiktokTokenServiceImpl implements TiktokTokenService {

    private final SocialMediaAccountRepository accountRepository;

    private final RestClient rest = RestClient.create();

    @Value( "#{new Integer('${tiktok.tokenRefreshWindowMinutes}')}")
    private Integer tokenRefreshWindowMinutes;

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

        var window = Duration.ofMinutes(tokenRefreshWindowMinutes != null ? tokenRefreshWindowMinutes : 5);
        var now = Instant.now();
        if (account.getTokenExpiresAt().isAfter(now.plus(window))) {
            return account.getAccessToken();
        }

        String refreshToken = account.getRefreshToken();
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new IllegalStateException("Missing refresh token for Tiktok account.");
        }

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_key", clientKey);
        form.add("client_secret", clientSecret);
        form.add("refresh_token", refreshToken);
        form.add("grant_type", "refresh_token");


        Map<String, Object> resp = rest.post()
                .uri(baseUri + "/v2/oauth/token/")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});


        if (resp == null || resp.isEmpty()) {
            throw new RuntimeException("Failed to exchange code for tokens.");
        }

        String respAccessToken = (String) resp.get("access_token");
        String respRefreshToken = (String) resp.getOrDefault("refresh_token", account.getRefreshToken());
        Number expiresInSec = (Number) resp.getOrDefault("expires_in", 3600);

        account.setAccessToken(respAccessToken);
        account.setRefreshToken(respRefreshToken);
        account.setTokenExpiresAt(Instant.now().plusSeconds(expiresInSec.longValue()));

        accountRepository.save(account);
        return respAccessToken;
    }
}
