package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.service.TiktokOAuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Base64;

@RestController
@RequestMapping("/api/tiktok/oauth")
@RequiredArgsConstructor
public class TiktokConnectController {
    private final TiktokOAuthService oAuthService;
    private final SecureRandom random = new SecureRandom();

    @GetMapping("/auth")
    public void auth(@RequestParam("username") String appUsername, HttpServletResponse response) {
        byte[] buf = new byte[16];
        random.nextBytes(buf);
        String state = appUsername + ":" + Base64.getUrlEncoder().withoutPadding().encodeToString(buf);

        URI uri = oAuthService.buildAuthorizationUri(state);
        response.setHeader("Location", uri.toString());
        response.setStatus(302);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code,
                                      @RequestParam(value = "state", required = false) String state) {
        String appUsername = state != null && state.contains(":") ? state.substring(0, state.indexOf(':')) : null;

        SocialMediaAccountEntity saved = oAuthService.exchangeCodeAndPersist(appUsername, state, code);
        return ResponseEntity.ok("TikTok connection OK. open_id=" + saved.getExternalId());
    }
}
