package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import com.deneme.influencerinsight.exception.OpenAiException;
import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;
import com.deneme.influencerinsight.service.OpenAiService;
import com.deneme.influencerinsight.service.SocialMediaAccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.base-url}")
    private String baseUrl;

    private final RestTemplate restTemplate;
    private final SocialMediaAccountService socialMediaAccountService;

    @Override
    public OpenAiResponse chat(OpenAiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OpenAiResponse> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
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

    @Override
    public String analyze(String username, String prompt) {
        List<SocialMediaAccountResponse> accounts = socialMediaAccountService.getAccounts(username);

        if (accounts.isEmpty()) {
            throw new OpenAiException("No connected social media accounts found for user: " + username);
        }

        StringBuilder contextBuilder = new StringBuilder("The user has connected the following social media accounts:\n");

        for (SocialMediaAccountResponse account : accounts) {
            contextBuilder.append("- Platform: ").append(account.getPlatform().name())
                    .append(", Username: ").append(account.getUsername()).append("\n")
                    .append(", SocialMediaUrl: ").append(account.getProfileUrl());
        }

        contextBuilder.append("\nNow answer the following question based on the accounts above:\n");
        contextBuilder.append(prompt);

        List<OpenAiMessageDto> messages = List.of(
                new OpenAiMessageDto("system", "You are a helpful social media strategist."),
                new OpenAiMessageDto("user", contextBuilder.toString())
        );

        OpenAiRequest request = new OpenAiRequest();
        request.setModel("gpt-3.5-turbo");
        request.setMessages(messages);
        request.setTemperature(0.7);

        OpenAiResponse response = chat(request);

        return response.getChoices()
                .stream()
                .findFirst()
                .map(choice -> choice.getMessage().getContent())
                .orElseThrow(() -> new OpenAiException("No response received from OpenAI"));
    }


}
