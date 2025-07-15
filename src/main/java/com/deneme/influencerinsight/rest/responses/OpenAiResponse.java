package com.deneme.influencerinsight.rest.responses;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private int index;
        private OpenAiMessageDto message;
        private String finishReason;
    }
}
