package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.rest.requests.OpenAiRequest;
import com.deneme.influencerinsight.rest.responses.openapi.OpenAiResponse;
import com.deneme.influencerinsight.service.OpenAiService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    @Operation(summary = "Chat with OpenAI", description = "Send prompt and get response from OpenAI API")
    public ResponseEntity<OpenAiResponse> chat(@RequestBody OpenAiRequest request) {
        OpenAiResponse response = openAiService.chat(request);
        return ResponseEntity.ok(response);
    }

}