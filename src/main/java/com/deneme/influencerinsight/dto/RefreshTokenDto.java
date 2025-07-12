package com.deneme.influencerinsight.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
public class RefreshTokenDto {
    private Long id;
    private String token;
    private Instant expiryDate;
    private Long userId;
}
