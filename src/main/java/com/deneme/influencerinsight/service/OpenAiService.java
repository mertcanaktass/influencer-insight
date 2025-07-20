package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;

public interface OpenAiService {
    OpenAiResponse chat(OpenAiRequest request);
    String analyze(String username, String prompt);
}
