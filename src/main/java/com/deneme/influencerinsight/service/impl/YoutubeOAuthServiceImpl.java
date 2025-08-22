package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import com.deneme.influencerinsight.service.YoutubeOAuthService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static com.deneme.influencerinsight.mapper.UserMapper.userResponseToEntity;

@Service
@RequiredArgsConstructor
public class YoutubeOAuthServiceImpl implements YoutubeOAuthService {

    @Value("${google.youtube.client-id}")
    private String clientId;
    @Value("${google.youtube.client-secret}")
    private String clientSecret;
    @Value("${google.youtube.redirect-uri}")
    private String redirectUri;
    @Value("${google.youtube.scopes}")
    private String scopesCsv;

    private final UserService userService;
    private final SocialMediaAccountRepository accountRepo;

    public String buildAuthorizationUrl(String state) {
        String scopes = Arrays.stream(scopesCsv.split(",")).map(String::trim).collect(Collectors.joining(" "));
        return UriComponentsBuilder.fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("access_type", "offline")
                .queryParam("prompt", "consent")
                .queryParam("scope", scopes)
                .queryParam("state", state)
                .build(true).toUriString();
    }

    public void exchangeCodeAndSaveAccount(String username, String code) {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Missing username (state).");

        Map<String, String> form = Map.of(
                "code", code,
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "grant_type", "authorization_code"
        );

        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<String> entity = new HttpEntity<>(toFormData(form), headers);

        Map<String, Object> tokenBody = rt.exchange(
                "https://oauth2.googleapis.com/token", HttpMethod.POST, entity,
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        ).getBody();

        if (tokenBody == null || tokenBody.get("access_token") == null) {
            throw new RuntimeException("Failed to exchange code for tokens.");
        }

        String accessToken = (String) tokenBody.get("access_token");
        String refreshToken = (String) tokenBody.get("refresh_token");
        Integer expiresIn = (Integer) tokenBody.get("expires_in");
        Instant expiresAt = expiresIn != null ? Instant.now().plusSeconds(expiresIn) : null;

        String channelJson = fetchMyChannelJson(accessToken);
        String channelId = extractChannelId(channelJson);
        String channelTitle = extractChannelTitle(channelJson);
        String channelUrl = channelId != null ? "https://www.youtube.com/channel/" + channelId : null;

        UserResponse userResponse = userService.inquireUserWithUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        SocialMediaAccountEntity socialMediaAccountEntity = accountRepo.findAllByUserUsernameAndPlatform(username, SocialMediaPlatform.YOUTUBE).stream()
                .findFirst()
                .orElseGet(() -> SocialMediaAccountEntity.builder()
                        .platform(SocialMediaPlatform.YOUTUBE)
                        .user(userResponseToEntity(userResponse))
                        .build());

        socialMediaAccountEntity.setAccessToken(accessToken);
        if (refreshToken != null) socialMediaAccountEntity.setRefreshToken(refreshToken);
        socialMediaAccountEntity.setTokenExpiresAt(expiresAt);
        socialMediaAccountEntity.setExternalId(channelId);
        socialMediaAccountEntity.setUsername(channelTitle != null ? channelTitle : username);
        socialMediaAccountEntity.setProfileUrl(channelUrl);
        socialMediaAccountEntity.setExtraData(channelJson);
        socialMediaAccountEntity.setLastSyncedAt(Instant.now());

        accountRepo.save(socialMediaAccountEntity);
    }

    private String fetchMyChannelJson(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        return rt.exchange(
                "https://www.googleapis.com/youtube/v3/channels?mine=true&part=snippet,statistics,contentDetails",
                HttpMethod.GET, entity, String.class
        ).getBody();
    }

    private String extractChannelId(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode items = objectMapper.readTree(json).path("items");
            if (items.isArray() && !items.isEmpty()) return items.get(0).path("id").asText(null);
        } catch (Exception ignored) {
        }
        return null;
    }

    private String extractChannelTitle(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode items = objectMapper.readTree(json).path("items");
            if (items.isArray() && !items.isEmpty()) return items.get(0).path("snippet").path("title").asText(null);
        } catch (Exception ignored) {
        }
        return null;
    }

    private String toFormData(Map<String, String> map) {
        return map.entrySet().stream()
                .map(e -> UriUtils.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + UriUtils.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}

