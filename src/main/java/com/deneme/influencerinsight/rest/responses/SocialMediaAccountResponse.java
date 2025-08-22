package com.deneme.influencerinsight.rest.responses;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class SocialMediaAccountResponse {
    private Long id;
    private SocialMediaPlatform platform;
    private String username;
    private String profileUrl;
    private boolean hasAccessToken;
    private String extraData;
    private Instant lastSyncedAt;
}
