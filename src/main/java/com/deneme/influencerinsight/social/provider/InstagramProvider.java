package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.service.InstagramTokenService;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.deneme.influencerinsight.util.JsonUtils.*;

@Component
@RequiredArgsConstructor
public class InstagramProvider implements SocialPlatformProvider {

    private final InstagramTokenService instagramTokenService;
    private final ExternalApiClient externalApiClient;

    @Value("${instagram.graph-version:v21.0}")
    private String graphVer;

    @Override
    public boolean supports(SocialMediaPlatform platform) {
        return platform == SocialMediaPlatform.INSTAGRAM;
    }

    @Override
    public String fetchAccountSnapshotJson(SocialMediaAccountEntity account) {
        String token = instagramTokenService.ensureValidAccessToken(account);
        String igUserId = account.getExternalId();
        if (igUserId == null || igUserId.isBlank()) {
            throw new IllegalStateException("Missing ig_user_id on account.");
        }

        Map<String, Object> profile = externalApiClient.get(
                "Facebook Graph API",
                "https://graph.facebook.com/" + graphVer + "/" + igUserId,
                Map.of("fields", "username,profile_picture_url,followers_count,follows_count,media_count,name"),
                token,
                new ParameterizedTypeReference<>() {
                }
        );

        Map<String, Object> media = externalApiClient.get(
                "Facebook Graph API",
                "https://graph.facebook.com/" + graphVer + "/" + igUserId + "/media",
                Map.of("fields", "id,caption,media_type,media_url,thumbnail_url,permalink,timestamp,like_count,comments_count",
                        "limit", "10"),
                token,
                new ParameterizedTypeReference<>() {
                }
        );
        List<Map<String, Object>> mediaList = toListOfMaps(media.get("data"));

        List<Map<String, Object>> insights = new ArrayList<>();
        for (int i = 0; i < Math.min(5, mediaList.size()); i++) {
            String mediaId = getString(mediaList.get(i), "id");
            if (mediaId == null) continue;
            Map<String, Object> ins = externalApiClient.get(
                    "Facebook Graph API",
                    "https://graph.facebook.com/" + graphVer + "/" + mediaId + "/insights",
                    Map.of("metric", "impressions,reach,engagement", "period", "lifetime"),
                    token,
                    new ParameterizedTypeReference<>() {
                    }
            );
            insights.add(Map.of("mediaId", mediaId, "insights", ins));
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("platform", "INSTAGRAM");
        snapshot.put("profile", profile);
        snapshot.put("recentMedia", Map.of("data", mediaList));
        snapshot.put("mediaInsights", insights);

        return toJson(snapshot);
    }

}
