package com.deneme.influencerinsight.rest.responses.openapi;

import lombok.Data;

import java.util.List;

@Data
public class OpenAiResponse {
    private List<Choice> choices;
}
