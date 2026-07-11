package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.service.InstagramOAuthService;
import com.deneme.influencerinsight.service.OAuthStateService;
import com.deneme.influencerinsight.service.OAuthRedirectService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class InstagramConnectController {

    private final InstagramOAuthService instagramOAuthService;
    private final OAuthStateService oauthStateService;
    private final OAuthRedirectService oauthRedirectService;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(Authentication authentication) {
        String state = oauthStateService.createState(authentication.getName(), SocialMediaPlatform.INSTAGRAM);
        String url = instagramOAuthService.buildAuthorizationUrl(state);
        return ResponseEntity.ok(Map.of("authUrl", url));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         @RequestParam("state") String state,
                         HttpServletResponse response) throws java.io.IOException {
        try {
            String username = oauthStateService.consumeState(state, SocialMediaPlatform.INSTAGRAM);
            instagramOAuthService.exchangeCodeAndSaveAccount(username, code);
            oauthRedirectService.redirectToAccounts(response, SocialMediaPlatform.INSTAGRAM, true);
        } catch (RuntimeException exception) {
            log.warn("Instagram OAuth callback failed", exception);
            oauthRedirectService.redirectToAccounts(response, SocialMediaPlatform.INSTAGRAM, false);
        }
    }
}
