package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;

public interface OAuthStateService {

    String createState(String username, SocialMediaPlatform platform);

    String createState(String username, SocialMediaPlatform platform, String codeVerifier);

    String consumeState(String state, SocialMediaPlatform platform);

    OAuthStateData consumeStateData(String state, SocialMediaPlatform platform);
}
