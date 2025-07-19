package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import lombok.Data;

@Data
public class SocialMediaAccountRequest {
    private SocialMediaPlatform platform;
    private String username;
    private String profileUrl;
    private String accessToken;
}
