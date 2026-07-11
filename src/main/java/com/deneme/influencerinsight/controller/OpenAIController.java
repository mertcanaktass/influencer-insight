package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.rest.requests.InsightRequest;
import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;
import com.deneme.influencerinsight.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
@RequiredArgsConstructor
public class OpenAIController {

    private final OpenAiService openAiService;

    @PostMapping("/chat")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Chat with OpenAI", description = "Send prompt and get response from OpenAI API")
    public ResponseEntity<OpenAiResponse> chat(@Valid @RequestBody OpenAiRequest request) {
        OpenAiResponse response = openAiService.chat(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzeAccount(@Valid @RequestBody InsightRequest request,
                                                 Authentication authentication) {
        String username = authentication.getName();
        String response = openAiService.analyze(username, request.getPrompt());
        return ResponseEntity.ok(response);
    }

}
