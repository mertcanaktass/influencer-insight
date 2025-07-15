package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.OpenAiResponse;

public interface OpenAiService {
    OpenAiResponse chat(OpenAiRequest request);
}
