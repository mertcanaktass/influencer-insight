package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.UserService;
import com.deneme.influencerinsight.service.YoutubeOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.deneme.influencerinsight.mapper.UserMapper.userResponseToEntity;
import static com.deneme.influencerinsight.util.HttpUtils.getAuthed;
import static com.deneme.influencerinsight.util.HttpUtils.postForm;
import static com.deneme.influencerinsight.util.JsonUtils.*;

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

    @Override
    public String buildAuthorizationUrl(String state) {
        String scopes = Arrays.stream(scopesCsv.split(","))
                .map(String::trim)
                .collect(Collectors.joining(" "));
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

    @Override
    public void exchangeCodeAndSaveAccount(String username, String code) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Missing username (state).");
        }

        Map<String, String> form = Map.of(
                "code", code,
                "client_id", clientId,
                "client_secret", clientSecret,
                "redirect_uri", redirectUri,
                "grant_type", "authorization_code"
        );

        Map<String, Object> tokenBody = postForm(
                "https://oauth2.googleapis.com/token",
                toFormData(form),
                new ParameterizedTypeReference<>() {
                }
        );

        if (tokenBody.get("access_token") == null) {
            throw new RuntimeException("Failed to exchange code for tokens.");
        }

        String accessToken = String.valueOf(tokenBody.get("access_token"));
        String refreshToken = (tokenBody.get("refresh_token") != null)
                ? String.valueOf(tokenBody.get("refresh_token"))
                : null;

        Object expObj = tokenBody.get("expires_in");
        Integer expiresIn = null;
        if (expObj instanceof Integer i) {
            expiresIn = i;
        } else if (expObj instanceof String s && !s.isBlank()) {
            try {
                expiresIn = Integer.parseInt(s);
            } catch (NumberFormatException ignored) { /* no-op */ }
        }

        Instant expiresAt = null;
        if (expiresIn != null) {
            expiresAt = Instant.now().plusSeconds(expiresIn);
        }

        Map<String, Object> channel = getAuthed(
                "https://www.googleapis.com/youtube/v3/channels",
                accessToken,
                Map.of("mine", "true", "part", "snippet,statistics,contentDetails"),
                new ParameterizedTypeReference<>() {
                }
        );

        String channelId = extractChannelId(channel);
        String channelTitle = extractChannelTitle(channel);
        String channelUrl = (channelId != null) ? "https://www.youtube.com/channel/" + channelId : null;

        UserResponse userResponse = userService.inquireUserWithUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        SocialMediaAccountEntity socialMediaAccountEntity = accountRepo
                .findAllByUserUsernameAndPlatform(username, SocialMediaPlatform.YOUTUBE)
                .stream()
                .findFirst()
                .orElseGet(() -> SocialMediaAccountEntity.builder()
                        .platform(SocialMediaPlatform.YOUTUBE)
                        .user(userResponseToEntity(userResponse))
                        .build());

        socialMediaAccountEntity.setAccessToken(accessToken);
        if (refreshToken != null) {
            socialMediaAccountEntity.setRefreshToken(refreshToken);
        }
        socialMediaAccountEntity.setTokenExpiresAt(expiresAt);
        socialMediaAccountEntity.setExternalId(channelId);
        socialMediaAccountEntity.setUsername(channelTitle != null ? channelTitle : username);
        socialMediaAccountEntity.setProfileUrl(channelUrl);
        socialMediaAccountEntity.setExtraData(toJson(channel));
        socialMediaAccountEntity.setLastSyncedAt(Instant.now());

        accountRepo.save(socialMediaAccountEntity);
    }

    private String extractChannelId(Map<String, Object> channel) {
        List<Map<String, Object>> items = toListOfMaps(channel.get("items"));
        if (items.isEmpty()) return null;
        return getString(items.getFirst(), "id");
    }

    private String extractChannelTitle(Map<String, Object> channel) {
        List<Map<String, Object>> items = toListOfMaps(channel.get("items"));
        if (items.isEmpty()) return null;
        Map<String, Object> snippet = toMap(items.getFirst().get("snippet"));
        return getString(snippet, "title");
    }

    private String toFormData(Map<String, String> map) {
        return map.entrySet().stream()
                .map(e -> UriUtils.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                        UriUtils.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
    }
}
