package com.deneme.influencerinsight.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordChangeRequest {
    @NotBlank
    @Size(max = 72)
    private String oldPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;
}
