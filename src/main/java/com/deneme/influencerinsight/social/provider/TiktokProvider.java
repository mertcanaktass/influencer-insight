package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.TiktokTokenService;
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

import java.util.LinkedHashMap;
import java.util.Map;

import static com.deneme.influencerinsight.util.JsonUtils.toJson;

@Component
@RequiredArgsConstructor
public class TiktokProvider implements SocialPlatformProvider {

    private SocialMediaAccountRepository accountRepository;
    private final TiktokTokenService tiktokTokenService;

    private static final String BASE = "https://open.tiktokapis.com";

    @Override
    public boolean supports(SocialMediaPlatform platform) {
        return platform == SocialMediaPlatform.TIKTOK;
    }

    @Override
    public String fetchAccountSnapshotJson(String ownerUsername, String ignoredAccessToken) throws Exception {
        SocialMediaAccountEntity account = accountRepository
                .findAllByUserUsernameAndPlatform(ownerUsername, SocialMediaPlatform.TIKTOK)
                .stream().findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tiktok account not linked for user: " + ownerUsername));

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
        UriComponentsBuilder b = UriComponentsBuilder.fromUriString(url);
        params.forEach(b::queryParam);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        RestTemplate rt = new RestTemplate();
        ResponseEntity<Map<String, Object>> res = rt.exchange(
                b.build(true).toUri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {}
        );

        if (!res.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("GET (auth) failed: " + url + " status=" + res.getStatusCode());
        }
        Map<String, Object> body = res.getBody();
        return body == null ? Map.of() : body;
    }
}
