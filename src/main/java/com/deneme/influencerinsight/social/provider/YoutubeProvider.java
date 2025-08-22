package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.YoutubeTokenService;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class YoutubeProvider implements SocialPlatformProvider {

    private final SocialMediaAccountRepository accountRepository;
    private final YoutubeTokenService youtubeTokenService;

    @Override
    public boolean supports(SocialMediaPlatform platform) {
        return platform == SocialMediaPlatform.YOUTUBE;
    }

    @Override
    public String fetchAccountSnapshotJson(String username, String ignoredAccessToken) throws Exception {
        Optional<SocialMediaAccountEntity> optional = accountRepository
                .findAllByUserUsernameAndPlatform(username, SocialMediaPlatform.YOUTUBE)
                .stream().findFirst();

        if (optional.isEmpty()) {
            throw new IllegalArgumentException("YouTube account not linked for user: " + username);
        }

        SocialMediaAccountEntity account = optional.get();
        String accessToken = youtubeTokenService.ensureValidAccessToken(account);

        String channelJson = getJson(accessToken,
                "https://www.googleapis.com/youtube/v3/channels?mine=true&part=snippet,statistics,contentDetails");

        String uploadsPlaylistId = extractUploadsPlaylistId(channelJson);
        String playlistItemsJson;
        if (uploadsPlaylistId == null) {
            playlistItemsJson = "null";
        } else {
            playlistItemsJson = getJson(accessToken,
                    "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet,contentDetails&maxResults=10&playlistId=" + uploadsPlaylistId);
        }

        List<String> videoIds = uploadsPlaylistId == null ? new ArrayList<>() : extractVideoIds(playlistItemsJson);

        String videosJson;
        if (videoIds.isEmpty()) {
            videosJson = "[]";
        } else {
            String joined = String.join(",", videoIds);
            videosJson = getJson(accessToken,
                    "https://www.googleapis.com/youtube/v3/videos?part=snippet,contentDetails,statistics&id=" + joined);
        }

        return "{\"channel\": " + channelJson + ", \"recentPlaylistItems\": " + playlistItemsJson + ", \"videos\": " + videosJson + "}";
    }

    private String getJson(String accessToken, String url) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        return response.getBody();
    }

    private String extractUploadsPlaylistId(String channelJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(channelJson);
        JsonNode items = root.path("items");
        if (items.isArray() && !items.isEmpty()) {
            return items.get(0).path("contentDetails").path("relatedPlaylists").path("uploads").asText(null);
        }
        return null;
    }

    private List<String> extractVideoIds(String playlistItemsJson) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(playlistItemsJson);
        JsonNode items = root.path("items");

        List<String> ids = new ArrayList<>();
        if (items.isArray()) {
            Iterator<JsonNode> it = items.elements();
            while (it.hasNext()) {
                JsonNode node = it.next();
                String id = node.path("contentDetails").path("videoId").asText(null);
                if (id != null) {
                    ids.add(id);
                }
            }
        }
        return ids;
    }
}
