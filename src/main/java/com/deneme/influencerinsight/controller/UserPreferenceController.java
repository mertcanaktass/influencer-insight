package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.enums.ThemePreference;
import com.deneme.influencerinsight.rest.requests.UpdateThemePreferenceRequest;
import com.deneme.influencerinsight.rest.responses.ThemePreferenceResponse;
import com.deneme.influencerinsight.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
