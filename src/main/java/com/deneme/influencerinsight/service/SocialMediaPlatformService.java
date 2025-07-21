package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.dto.SocialMediaPlatformDto;
import com.deneme.influencerinsight.rest.requests.SocialMediaPlatformRequest;

public interface SocialMediaPlatformService {

    public SocialMediaPlatformDto getSocialMediaPlatform(String shortCode);

    public SocialMediaPlatformDto createSocialMediaPlatform(SocialMediaPlatformRequest request);

    public SocialMediaPlatformDto updateSocialMediaPlatform(SocialMediaPlatformRequest request);

    public SocialMediaPlatformDto deactivateSocialMediaPlatform(SocialMediaPlatformRequest request);

}
