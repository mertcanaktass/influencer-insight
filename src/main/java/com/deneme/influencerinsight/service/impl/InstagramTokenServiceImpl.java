package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.service.InstagramTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InstagramTokenServiceImpl implements InstagramTokenService {

    private final SocialMediaAccountRepository accountRepository;

    @Override
    public String ensureValidAccessToken(SocialMediaAccountEntity account) {
        if (account == null) throw new IllegalArgumentException("Account cannot be null.");
        String token = account.getAccessToken();
        if (token == null || token.isBlank()) {
            throw new IllegalStateException("Missing Instagram page access token.");
        }
        return token;
    }
}
