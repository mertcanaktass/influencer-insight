package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.enums.ThemePreference;
import jakarta.validation.constraints.NotNull;

public record UpdateThemePreferenceRequest(
        @NotNull ThemePreference themePreference
) {
}
