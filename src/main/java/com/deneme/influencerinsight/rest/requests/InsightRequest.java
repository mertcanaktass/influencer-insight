package com.deneme.influencerinsight.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class InsightRequest {
    @NotBlank
    @Size(max = 4_000)
    private String prompt;
}
