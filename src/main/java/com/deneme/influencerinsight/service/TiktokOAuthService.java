package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

import java.net.URI;

public interface TiktokOAuthService {

    String generateCodeVerifier();

    URI buildAuthorizationUri(String state, String codeVerifier);

    SocialMediaAccountEntity exchangeCodeAndPersist(String username, String code, String codeVerifier);

}
