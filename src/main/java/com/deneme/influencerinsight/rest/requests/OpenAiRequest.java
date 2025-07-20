package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiRequest {
    private String model;
    private List<OpenAiMessageDto> messages;
    private double temperature;
}