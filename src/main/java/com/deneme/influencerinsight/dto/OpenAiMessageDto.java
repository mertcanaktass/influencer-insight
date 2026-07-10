package com.deneme.influencerinsight.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiMessageDto {

    @NotBlank
    @Size(max = 32)
    private String role;

    @NotBlank
    @Size(max = 20_000)
    private String content;
}
