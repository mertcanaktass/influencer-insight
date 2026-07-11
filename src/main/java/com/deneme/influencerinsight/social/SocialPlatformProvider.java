package com.deneme.influencerinsight.social;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;

public interface SocialPlatformProvider {

    boolean supports(SocialMediaPlatform platform);

    String fetchAccountSnapshotJson(SocialMediaAccountEntity account);
}
