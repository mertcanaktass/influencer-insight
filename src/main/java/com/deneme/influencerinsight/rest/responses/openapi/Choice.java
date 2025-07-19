package com.deneme.influencerinsight.rest.responses.openapi;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Choice {
    private int index;
    private OpenAiMessageDto message;

    @JsonProperty("finish_reason")
    private String finishReason;
}
