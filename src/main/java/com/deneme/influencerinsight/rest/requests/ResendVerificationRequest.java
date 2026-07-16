package com.deneme.influencerinsight.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResendVerificationRequest {
    @NotBlank
    @Size(max = 50)
    private String username;
}
