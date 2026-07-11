package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.ThemePreference;
import com.deneme.influencerinsight.rest.requests.UpdateThemePreferenceRequest;
import com.deneme.influencerinsight.rest.responses.ThemePreferenceResponse;
import com.deneme.influencerinsight.rest.responses.SocialConnectionConsentResponse;
import com.deneme.influencerinsight.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.security.Principal;

/** Oturumdaki kullanıcının cihazlar arası saklanan arayüz tercihleri. */
@RestController
@RequestMapping("/api/users/me/preferences")
@RequiredArgsConstructor
public class UserPreferenceController {

    private final UserService userService;

    @GetMapping("/theme")
    public ThemePreferenceResponse getThemePreference(Principal principal) {
        return new ThemePreferenceResponse(userService.getThemePreference(principal.getName()));
    }

    @PutMapping("/theme")
    public ResponseEntity<ThemePreferenceResponse> updateThemePreference(
            @Valid @RequestBody UpdateThemePreferenceRequest request,
            Principal principal
    ) {
        ThemePreference preference = userService.updateThemePreference(
                principal.getName(), request.themePreference());
        return ResponseEntity.ok(new ThemePreferenceResponse(preference));
    }

    @GetMapping("/social-connection-consent")
    public SocialConnectionConsentResponse getSocialConnectionConsent(Principal principal) {
        return userService.getSocialConnectionConsent(principal.getName());
    }

    @PutMapping("/social-connection-consent")
    public SocialConnectionConsentResponse acceptSocialConnectionConsent(Principal principal) {
        return userService.acceptSocialConnectionConsent(principal.getName());
    }

    @GetMapping(value = "/data-export", produces = "application/json")
    public ResponseEntity<java.util.Map<String, Object>> exportData(Principal principal) {
        return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=personal-data-export.json")
                .body(userService.exportUserData(principal.getName()));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAccount(@Valid @RequestBody com.deneme.influencerinsight.rest.requests.DeleteAccountRequest request,
                                               Principal principal) {
        userService.deleteUserAccount(principal.getName(), request.currentPassword());
        return ResponseEntity.noContent().build();
    }
}
