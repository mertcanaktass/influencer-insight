package com.deneme.influencerinsight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiMessageDto {
    private String role;    // "user", "assistant", "system"
    private String content;
}
