package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.service.YoutubeOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/youtube/oauth")
@RequiredArgsConstructor
public class YoutubeConnectController {

    private final YoutubeOAuthService youtubeOAuthService;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(Authentication authentication) {
        // Uç permitAll; token'sız çağrıda authentication null olabilir (NPE'yi önle).
        String state = authentication != null ? authentication.getName() : "anonymous";
        String url = youtubeOAuthService.buildAuthorizationUrl(state);
        return ResponseEntity.ok(Map.of("authUrl", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {
        youtubeOAuthService.exchangeCodeAndSaveAccount(state, code);
        return ResponseEntity.ok("YouTube account linked for user: " + state);
    }
}

