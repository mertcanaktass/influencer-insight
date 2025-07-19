package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.rest.requests.SocialMediaAccountRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;

import java.util.Objects;

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
        if (Objects.isNull(entity)) return null;

        SocialMediaAccountResponse response = new SocialMediaAccountResponse();
        response.setId(entity.getId());
        response.setPlatform(entity.getPlatform());
        response.setUsername(entity.getUsername());
        response.setProfileUrl(entity.getProfileUrl());
        return response;
    }
}
