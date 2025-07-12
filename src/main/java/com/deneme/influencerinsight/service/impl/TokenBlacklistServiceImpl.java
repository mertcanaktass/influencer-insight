package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.TokenBlacklistEntity;
import com.deneme.influencerinsight.repository.TokenBlacklistRepository;
import com.deneme.influencerinsight.service.TokenBlacklistService;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public TokenBlacklistServiceImpl(TokenBlacklistRepository tokenBlacklistRepository) {
        this.tokenBlacklistRepository = tokenBlacklistRepository;
    }

    @Override
    public void blacklistToken(String token, Date expirationDate) {
        if (!tokenBlacklistRepository.existsByToken(token)) {
            TokenBlacklistEntity entity = TokenBlacklistEntity.builder()
                    .token(token)
                    .expirationDate(expirationDate)
                    .build();
            tokenBlacklistRepository.save(entity);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }
}

