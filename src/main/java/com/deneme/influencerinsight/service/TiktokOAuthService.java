package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

import java.net.URI;

public interface TiktokOAuthService {

    URI buildAuthorizationUri(String state);

    SocialMediaAccountEntity exchangeCodeAndPersist(String appUsername, String stateIgnored, String code);

}
