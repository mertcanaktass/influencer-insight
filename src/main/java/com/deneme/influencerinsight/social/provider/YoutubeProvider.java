package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.service.YoutubeTokenService;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.deneme.influencerinsight.util.JsonUtils.*;

@Component
@RequiredArgsConstructor
public class YoutubeProvider implements SocialPlatformProvider {

    private final YoutubeTokenService youtubeTokenService;
    private final ExternalApiClient externalApiClient;

    @Override
    public boolean supports(SocialMediaPlatform platform) {
        return platform == SocialMediaPlatform.YOUTUBE;
    }

    @Override
    public String fetchAccountSnapshotJson(SocialMediaAccountEntity account) {
        String accessToken = youtubeTokenService.ensureValidAccessToken(account);

        Map<String, Object> channel = getAuthed(
                "https://www.googleapis.com/youtube/v3/channels",
                accessToken,
                Map.of("mine", "true", "part", "snippet,statistics,contentDetails")
        );

        String uploadsPlaylistId = extractUploadsPlaylistId(channel);

        Map<String, Object> playlistItems = Map.of();
        List<String> videoIds = new ArrayList<>();
        if (uploadsPlaylistId != null) {
            playlistItems = getAuthed(
                    "https://www.googleapis.com/youtube/v3/playlistItems",
                    accessToken,
                    Map.of("part", "snippet,contentDetails", "maxResults", "10", "playlistId", uploadsPlaylistId)
            );
            videoIds = extractVideoIds(playlistItems);
        }

        Map<String, Object> videos = Map.of();
        if (!videoIds.isEmpty()) {
            String joined = String.join(",", videoIds);
            videos = getAuthed(
                    "https://www.googleapis.com/youtube/v3/videos",
                    accessToken,
                    Map.of("part", "snippet,contentDetails,statistics", "id", joined)
            );
        }

        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("channel", channel);
        snapshot.put("recentPlaylistItems", playlistItems);
        snapshot.put("videos", videos);

        return toJson(snapshot);
    }

    private Map<String, Object> getAuthed(String url, String token, Map<String, String> params) {
        return externalApiClient.get("YouTube Data API", url, params, token,
                new ParameterizedTypeReference<>() {
                });
    }

    private String extractUploadsPlaylistId(Map<String, Object> channel) {
        List<Map<String, Object>> items = toListOfMaps(channel.get("items"));
        if (items.isEmpty()) return null;
        Map<String, Object> first = items.getFirst();
        Map<String, Object> contentDetails = toMap(first.get("contentDetails"));
        Map<String, Object> relatedPlaylists = toMap(contentDetails.get("relatedPlaylists"));
        String uploads = getString(relatedPlaylists, "uploads");
        return (uploads == null || uploads.isBlank()) ? null : uploads;
    }

    private List<String> extractVideoIds(Map<String, Object> playlistItems) {
        List<Map<String, Object>> items = toListOfMaps(playlistItems.get("items"));
        List<String> ids = new ArrayList<>();
        for (Map<String, Object> it : items) {
            Map<String, Object> cd = toMap(it.get("contentDetails"));
            String id = getString(cd, "videoId");
            if (id != null) ids.add(id);
        }
        return ids;
    }

}
