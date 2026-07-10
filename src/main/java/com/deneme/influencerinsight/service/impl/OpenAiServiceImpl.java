package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import com.deneme.influencerinsight.exception.OpenAiException;
import com.deneme.influencerinsight.integration.ExternalApiClient;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;
import com.deneme.influencerinsight.service.OpenAiService;
import com.deneme.influencerinsight.service.AiRateLimitService;
import com.deneme.influencerinsight.service.SocialMediaAccountService;
import com.deneme.influencerinsight.util.PromptBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiServiceImpl implements OpenAiService {

    @Value("${openai.api.key}")
    private String apiKey;

    @Value("${openai.base-url}")
    private String baseUrl;

    @Value("${openai.model}")
    private String defaultModel;

    private final ExternalApiClient externalApiClient;
    private final SocialMediaAccountService socialMediaAccountService;
    private final SocialMediaAccountRepository socialMediaAccountRepository;
    private final AiRateLimitService aiRateLimitService;

    @Override
    public OpenAiResponse chat(OpenAiRequest request) {
        request.setModel(defaultModel);
        try {
            return externalApiClient.postJson(
                    "OpenAI",
                    URI.create(baseUrl + "/chat/completions"),
                    apiKey,
                    request,
                    OpenAiResponse.class);
        } catch (Exception ex) {
            throw new OpenAiException("Error while calling OpenAI API", ex);
        }
    }

    @Override
    public String analyze(String username, String userPrompt) {
        aiRateLimitService.checkLimit(username);
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

        if (ai.getChoices() != null && !ai.getChoices().isEmpty() && ai.getChoices().getFirst().getMessage() != null) {
            return ai.getChoices().getFirst().getMessage().getContent();
        }
        return "Analiz üretilemedi.";
    }

}
