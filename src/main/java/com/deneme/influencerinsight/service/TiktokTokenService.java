package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

public interface TiktokTokenService {
    String ensureValidAccessToken(SocialMediaAccountEntity account);
}
