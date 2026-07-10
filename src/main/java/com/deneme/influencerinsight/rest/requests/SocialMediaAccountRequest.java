package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SocialMediaAccountRequest {
    @NotNull
    private SocialMediaPlatform platform;

    @NotBlank
    @Size(max = 100)
    private String username;

    @Size(max = 2_048)
    private String profileUrl;

    @Size(max = 4_000)
    private String accessToken;
}
