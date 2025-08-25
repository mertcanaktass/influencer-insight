package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.YoutubeTokenService;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.deneme.influencerinsight.util.JsonUtils.*;

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
    public String fetchAccountSnapshotJson(String ownerUsername, String ignoredAccessToken) throws Exception {
        SocialMediaAccountEntity account = accountRepository
                .findAllByUserUsernameAndPlatform(ownerUsername, SocialMediaPlatform.YOUTUBE)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("YouTube account not linked for user: " + ownerUsername));

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
        UriComponentsBuilder b = UriComponentsBuilder.fromUriString(url);
        params.forEach(b::queryParam);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<Map<String, Object>> res = rt.exchange(
                b.build(true).toUri(), HttpMethod.GET, new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("GET (auth) failed: " + url + " status=" + res.getStatusCode());
        }
        Map<String, Object> body = res.getBody();
        return body == null ? Map.of() : body;
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
