package com.deneme.influencerinsight.social;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SocialProviderRegistry {

    private final List<SocialPlatformProvider> providers;

    public SocialProviderRegistry(List<SocialPlatformProvider> providers) {
        this.providers = providers;
    }

    public SocialPlatformProvider getProvider(SocialMediaPlatform platform) {
        return providers.stream()
                .filter(p -> p.supports(platform))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No provider for platform: " + platform));
    }
}
