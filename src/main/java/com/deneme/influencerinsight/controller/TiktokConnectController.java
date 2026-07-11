package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.service.OAuthStateService;
import com.deneme.influencerinsight.service.OAuthStateData;
import com.deneme.influencerinsight.service.OAuthRedirectService;
import com.deneme.influencerinsight.service.TiktokOAuthService;
import com.deneme.influencerinsight.service.UserService;
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
@RequestMapping("/api/tiktok/oauth")
@RequiredArgsConstructor
@Slf4j
public class TiktokConnectController {

    private final TiktokOAuthService tiktokOAuthService;
    private final OAuthStateService oauthStateService;
    private final OAuthRedirectService oauthRedirectService;
    private final UserService userService;

    @GetMapping("/url")
    public ResponseEntity<Map<String, String>> getAuthorizationUrl(Authentication authentication) {
        userService.requireSocialConnectionConsent(authentication.getName());
        String codeVerifier = tiktokOAuthService.generateCodeVerifier();
        String state = oauthStateService.createState(
                authentication.getName(), SocialMediaPlatform.TIKTOK, codeVerifier);
        return ResponseEntity.ok(Map.of("authUrl", tiktokOAuthService.buildAuthorizationUri(state, codeVerifier).toString()));
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         @RequestParam("state") String state,
                         HttpServletResponse response) throws java.io.IOException {
        try {
            OAuthStateData stateData = oauthStateService.consumeStateData(state, SocialMediaPlatform.TIKTOK);
            tiktokOAuthService.exchangeCodeAndPersist(stateData.username(), code, stateData.codeVerifier());
            oauthRedirectService.redirectToAccounts(response, SocialMediaPlatform.TIKTOK, true);
        } catch (RuntimeException exception) {
            log.warn("TikTok OAuth callback failed", exception);
            oauthRedirectService.redirectToAccounts(response, SocialMediaPlatform.TIKTOK, false);
        }
    }
}
