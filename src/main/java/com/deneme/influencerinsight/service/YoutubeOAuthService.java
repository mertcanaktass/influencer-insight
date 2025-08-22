package com.deneme.influencerinsight.service;

public interface YoutubeOAuthService {

    String buildAuthorizationUrl(String state);

    void exchangeCodeAndSaveAccount(String username, String code);
}
