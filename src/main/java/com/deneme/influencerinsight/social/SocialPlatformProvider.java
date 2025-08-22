package com.deneme.influencerinsight.social;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;

public interface SocialPlatformProvider {

    boolean supports(SocialMediaPlatform platform);

    String fetchAccountSnapshotJson(String username, String accessToken) throws Exception;
}
