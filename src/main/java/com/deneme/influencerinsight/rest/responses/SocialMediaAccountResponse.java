package com.deneme.influencerinsight.rest.responses;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import lombok.Data;

@Data
public class SocialMediaAccountResponse {
    private Long id;
    private SocialMediaPlatform platform;
    private String username;
    private String profileUrl;
}
