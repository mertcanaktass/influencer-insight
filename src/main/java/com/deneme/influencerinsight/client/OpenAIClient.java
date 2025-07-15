package com.deneme.influencerinsight.client;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class OpenAIClient {

    private final WebClient openAiWebClient;

    public Mono<String> chatWithOpenAI(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", "gpt-3.5-turbo",
                "messages", new Object[]{
                        Map.of("role", "user", "content", prompt)
                }
        );

        return openAiWebClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class);
    }
}
