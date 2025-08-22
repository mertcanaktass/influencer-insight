package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

public interface YoutubeTokenService {

    String ensureValidAccessToken(SocialMediaAccountEntity account);
}
