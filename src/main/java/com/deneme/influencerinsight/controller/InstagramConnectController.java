package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.service.InstagramOAuthService;
import com.deneme.influencerinsight.service.OAuthStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/instagram/oauth")
@RequiredArgsConstructor
public class InstagramConnectController {

    private final InstagramOAuthService instagramOAuthService;
    private final OAuthStateService oauthStateService;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(Authentication authentication) {
        String state = oauthStateService.createState(authentication.getName(), SocialMediaPlatform.INSTAGRAM);
        String url = instagramOAuthService.buildAuthorizationUrl(state);
        return ResponseEntity.ok(Map.of("authUrl", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {
        String username = oauthStateService.consumeState(state, SocialMediaPlatform.INSTAGRAM);
        instagramOAuthService.exchangeCodeAndSaveAccount(username, code);
        return ResponseEntity.ok("Instagram account linked for user: " + username);
    }
}
