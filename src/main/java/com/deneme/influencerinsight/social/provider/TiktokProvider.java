package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.service.TiktokTokenService;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

import static com.deneme.influencerinsight.util.JsonUtils.toJson;

@Component
@RequiredArgsConstructor
public class TiktokProvider implements SocialPlatformProvider {

    private final TiktokTokenService tiktokTokenService;
    private final ExternalApiClient externalApiClient;

    private static final String BASE = "https://open.tiktokapis.com";

    @Override
    public boolean supports(SocialMediaPlatform platform) {
        return platform == SocialMediaPlatform.TIKTOK;
    }

    @Override
    public String fetchAccountSnapshotJson(SocialMediaAccountEntity account) {
        String accessToken = tiktokTokenService.ensureValidAccessToken(account);

        Map<String, Object> user = getAuthed(
                BASE + "/v2/user/info/",
                accessToken,
                Map.of("fields", "open_id,username,display_name,avatar_url")
        );

        Map<String, Object> videos = getAuthed(
                BASE + "/v2/video/list/",
                accessToken,
                Map.of("page_size", "10")
        );

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("user", user);
        snapshot.put("videos", videos);

        return toJson(snapshot);
    }

    private Map<String, Object> getAuthed(String url, String token, Map<String, String> params) {
        return externalApiClient.get("TikTok API", url, params, token,
                new ParameterizedTypeReference<>() {
                });
    }
}
