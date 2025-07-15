package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.OpenAiResponse;
import com.deneme.influencerinsight.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private static final String OPENAI_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    public OpenAiResponse chat(OpenAiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                OPENAI_URL,
                HttpMethod.POST,
                entity,
                OpenAiResponse.class
        );

        return response.getBody();
    }
}
