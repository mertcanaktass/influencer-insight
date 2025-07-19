package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.exception.OpenAiException;
import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;
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
    @Value("${openai.base-url}")
    private String baseUrl;

    @Override
    public OpenAiResponse chat(OpenAiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                    baseUrl,
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                log.error("OpenAI API failed. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new OpenAiException("OpenAI API request failed with status: " + response.getStatusCode());
            }
        } catch (Exception ex) {
            log.error("Error while calling OpenAI API", ex);
            throw new OpenAiException("Error while calling OpenAI API", ex);
        }
    }
}
