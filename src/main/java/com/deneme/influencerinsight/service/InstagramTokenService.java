package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

public interface InstagramTokenService {
    String ensureValidAccessToken(SocialMediaAccountEntity account);
}