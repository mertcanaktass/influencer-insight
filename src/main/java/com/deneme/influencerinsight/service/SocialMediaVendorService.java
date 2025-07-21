package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.dto.SocialMediaVendorDto;
import com.deneme.influencerinsight.rest.requests.SocialMediaVendorRequest;

public interface SocialMediaVendorService {

    public SocialMediaVendorDto createSocialMediaVendor(SocialMediaVendorRequest request);

    public SocialMediaVendorDto updateSocialMediaVendor(SocialMediaVendorRequest request);

    public SocialMediaVendorDto deactivateSocialMediaVendor(SocialMediaVendorRequest request);
}
