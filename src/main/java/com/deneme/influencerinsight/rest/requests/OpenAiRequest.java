package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiRequest {
    @Size(max = 100)
    private String model;

    @NotEmpty
    @Size(max = 20)
    private List<@Valid OpenAiMessageDto> messages;

    @DecimalMin("0.0")
    @DecimalMax("2.0")
    private double temperature;
}
