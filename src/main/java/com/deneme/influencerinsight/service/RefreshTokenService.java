package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.UserEntity;

public interface RefreshTokenService {

    String createRefreshToken(UserEntity user);

    UserEntity consumeRefreshToken(String token);

    void revokeTokensForUser(UserEntity user);
}
