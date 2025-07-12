package com.deneme.influencerinsight.service;

import java.util.Date;

public interface TokenBlacklistService {
    void blacklistToken(String token, Date expirationDate);

    boolean isTokenBlacklisted(String token);
}

