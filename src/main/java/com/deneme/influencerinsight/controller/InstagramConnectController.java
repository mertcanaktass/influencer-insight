package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.service.InstagramOAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/instagram/oauth")
@RequiredArgsConstructor
public class InstagramConnectController {

    private final InstagramOAuthService instagramOAuthService;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(Authentication authentication) {
        String state = authentication != null ? authentication.getName() : "anonymous";
        String url = instagramOAuthService.buildAuthorizationUrl(state);
        return ResponseEntity.ok(Map.of("authUrl", url));
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {
        instagramOAuthService.exchangeCodeAndSaveAccount(state, code);
        return ResponseEntity.ok("Instagram account linked for user: " + state);
    }
}
