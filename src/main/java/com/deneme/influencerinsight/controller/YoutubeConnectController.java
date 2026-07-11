package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.service.OAuthStateService;
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
    private final OAuthStateService oauthStateService;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(Authentication authentication) {
        String state = oauthStateService.createState(authentication.getName(), SocialMediaPlatform.YOUTUBE);
        String url = youtubeOAuthService.buildAuthorizationUrl(state);
        return ResponseEntity.ok(Map.of("authUrl", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {
        String username = oauthStateService.consumeState(state, SocialMediaPlatform.YOUTUBE);
        youtubeOAuthService.exchangeCodeAndSaveAccount(username, code);
        return ResponseEntity.ok("YouTube account linked for user: " + username);
    }
}
