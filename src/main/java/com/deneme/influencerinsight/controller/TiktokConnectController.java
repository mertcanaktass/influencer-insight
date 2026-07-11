package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.service.OAuthStateService;
import com.deneme.influencerinsight.service.TiktokOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/tiktok/oauth")
@RequiredArgsConstructor
public class TiktokConnectController {

    private final TiktokOAuthService tiktokOAuthService;
    private final OAuthStateService oauthStateService;

    @GetMapping("/auth")
    public void auth(Authentication authentication, HttpServletResponse response) {
        String state = oauthStateService.createState(authentication.getName(), SocialMediaPlatform.TIKTOK);
        URI authorizationUri = tiktokOAuthService.buildAuthorizationUri(state);
        response.setHeader("Location", authorizationUri.toString());
        response.setStatus(HttpServletResponse.SC_FOUND);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> callback(@RequestParam("code") String code,
                                           @RequestParam("state") String state) {
        String username = oauthStateService.consumeState(state, SocialMediaPlatform.TIKTOK);
        SocialMediaAccountEntity saved = tiktokOAuthService.exchangeCodeAndPersist(username, code);
        return ResponseEntity.ok("TikTok connection OK. open_id=" + saved.getExternalId());
    }
}
