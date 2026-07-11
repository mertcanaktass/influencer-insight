package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.rest.requests.SocialMediaAccountRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;
import com.deneme.influencerinsight.rest.responses.SocialMediaSyncResponse;
import com.deneme.influencerinsight.service.SocialMediaAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/social-media")
@RequiredArgsConstructor
public class SocialMediaAccountController {

    private final SocialMediaAccountService socialMediaAccountService;

    @PostMapping
    public ResponseEntity<SocialMediaAccountResponse> addAccount(Authentication authentication,
                                                                 @Valid @RequestBody SocialMediaAccountRequest request) {
        String username = authentication.getName();
        SocialMediaAccountResponse response = socialMediaAccountService.addAccount(username, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SocialMediaAccountResponse>> getAccounts(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(socialMediaAccountService.getAccounts(username));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        socialMediaAccountService.deleteAccount(username, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/sync")
    public ResponseEntity<SocialMediaSyncResponse> syncAccount(Authentication authentication, @PathVariable Long id) {
        String username = authentication.getName();
        return ResponseEntity.ok(socialMediaAccountService.syncAccount(username, id));
    }

    @PostMapping("/sync")
    public ResponseEntity<List<SocialMediaSyncResponse>> syncAll(Authentication authentication) {
        String username = authentication.getName();
        return ResponseEntity.ok(socialMediaAccountService.syncAll(username));
    }
}
