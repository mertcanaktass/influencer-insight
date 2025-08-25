package com.deneme.influencerinsight.service;

public interface InstagramOAuthService {
    String buildAuthorizationUrl(String state);

    void exchangeCodeAndSaveAccount(String username, String code);
}
