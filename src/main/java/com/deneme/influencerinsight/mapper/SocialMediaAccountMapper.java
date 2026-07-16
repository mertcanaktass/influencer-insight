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

    public static SocialMediaAccountResponse entityToResponse(SocialMediaAccountEntity entity) {
        if (entity == null) return null;
        return SocialMediaAccountResponse.builder()
                .id(entity.getId())
                .platform(entity.getPlatform())
                .username(entity.getUsername())
                .profileUrl(entity.getProfileUrl())
                .hasAccessToken(entity.getAccessToken() != null && !entity.getAccessToken().isBlank())
                .lastSyncedAt(entity.getLastSyncedAt())
                .build();
    }
}
