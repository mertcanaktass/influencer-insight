package com.deneme.influencerinsight.rest.requests;

import com.deneme.influencerinsight.dto.OpenAiMessageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpenAiRequest {
    private String model; // örn: gpt-3.5-turbo veya gpt-4
    private List<OpenAiMessageDto> messages;
    private double temperature;
    /*
        | `temperature` | Açıklama                                |
        | ------------- | --------------------------------------- |
        | `0.0`         | En tutarlı ve tekrar eden cevaplar      |
        | `0.3`         | Hafif rastlantısallık, genelde önerilir |
        | `0.7`         | Dengeli rastgelelik ve tutarlılık       |
        | `1.0`         | Yaratıcılık artar, tutarlılık düşer     |
    */
}