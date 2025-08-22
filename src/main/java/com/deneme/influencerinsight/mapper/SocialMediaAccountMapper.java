package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.rest.requests.SocialMediaAccountRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;

public class SocialMediaAccountMapper {

    private SocialMediaAccountMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static SocialMediaAccountEntity requestToEntity(SocialMediaAccountRequest request) {
        if (request == null) return null;

        return SocialMediaAccountEntity.builder()
                .platform(request.getPlatform())
                .username(request.getUsername())
                .profileUrl(request.getProfileUrl())
                .accessToken(request.getAccessToken())
                .build();
    }

    public static SocialMediaAccountResponse entityToResponse(SocialMediaAccountEntity e) {
        if (e == null) return null;
        return SocialMediaAccountResponse.builder()
                .id(e.getId())
                .platform(e.getPlatform())
                .username(e.getUsername())
                .profileUrl(e.getProfileUrl())
                .hasAccessToken(e.getAccessToken() != null && !e.getAccessToken().isBlank())
                .extraData(e.getExtraData())
                .lastSyncedAt(e.getLastSyncedAt())
                .build();
    }
}
