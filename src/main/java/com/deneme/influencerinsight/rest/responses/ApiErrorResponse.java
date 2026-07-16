package com.deneme.influencerinsight.rest.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.Map;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiErrorResponse {

    Instant timestamp;
    int status;
    String code;
    String message;
    Map<String, String> fieldErrors;
}
