package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import com.deneme.influencerinsight.exception.OpenAiException;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;
import com.deneme.influencerinsight.service.OpenAiService;
import com.deneme.influencerinsight.service.SocialMediaAccountService;
import com.deneme.influencerinsight.util.PromptBuilder;
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

    @Value("${openai.model:gpt-4o-mini}")
    private String defaultModel;

    private final RestTemplate restTemplate;
    private final SocialMediaAccountService socialMediaAccountService;
    private final SocialMediaAccountRepository socialMediaAccountRepository;

    @Override
    public OpenAiResponse chat(OpenAiRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<OpenAiRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<OpenAiResponse> response;
        try {
            response = restTemplate.exchange(
                    baseUrl + "/v1/chat/completions",
                    HttpMethod.POST,
                    entity,
                    OpenAiResponse.class
            );
        } catch (Exception ex) {
            throw new OpenAiException("Error while calling OpenAI API", ex);
        }

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        }
        throw new OpenAiException("OpenAI API request failed with status: " + response.getStatusCode());
    }

    @Override
    public String analyze(String username, String userPrompt) {
        socialMediaAccountService.syncAll(username);

        List<SocialMediaAccountEntity> accounts = socialMediaAccountRepository.findAllByUserUsername(username);

        String prompt = PromptBuilder.buildAnalysisPrompt(username, userPrompt, accounts);

        OpenAiMessageDto system = new OpenAiMessageDto("system",
                "You are a world-class social media strategist. Keep answers concise but specific.");
        OpenAiMessageDto user = new OpenAiMessageDto("user", prompt);

        OpenAiRequest req = new OpenAiRequest();
        req.setModel(defaultModel);
        req.setMessages(List.of(system, user));
        req.setTemperature(0.3);

        OpenAiResponse ai = chat(req);

        if (ai.getChoices() != null && !ai.getChoices().isEmpty() && ai.getChoices().get(0).getMessage() != null) {
            return ai.getChoices().get(0).getMessage().getContent();
        }
        return "Analiz üretilemedi.";
    }

}
