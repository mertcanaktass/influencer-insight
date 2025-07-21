package com.deneme.influencerinsight.mapper;

import com.deneme.influencerinsight.dto.SocialMediaPlatformDto;
import com.deneme.influencerinsight.enums.Status;
import com.deneme.influencerinsight.model.social_media.SocialMediaPlatformEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SocialMediaPlatformMapper {

    public SocialMediaPlatformDto socialMediaPlatformEntityToSocialMediaPlatformDto(SocialMediaPlatformEntity socialMediaPlatformEntity) {
        if (Objects.nonNull(socialMediaPlatformEntity)) {
            return SocialMediaPlatformDto.builder()
                    .id(socialMediaPlatformEntity.getId())
                    .vendorName(socialMediaPlatformEntity.getSocialMediaVendor().getVendorName())
                    .createDate(socialMediaPlatformEntity.getCreateDate())
                    .createUserId(Objects.nonNull(socialMediaPlatformEntity.getCreateUserId()) ? socialMediaPlatformEntity.getCreateUserId() : null)
                    .updateDate(Objects.nonNull(socialMediaPlatformEntity.getUpdateDate()) ? socialMediaPlatformEntity.getUpdateDate(): null)
                    .updateUserId(Objects.nonNull(socialMediaPlatformEntity.getUpdateUserId()) ? socialMediaPlatformEntity.getUpdateUserId() : null)
                    .status(Status.getStatusById(socialMediaPlatformEntity.getStatus()))
                    .build();
        }
        else {
            throw new RuntimeException("Could not map SocialMediaPlatformEntity to SocialMediaPlatformDto");
        }
    }

    public SocialMediaPlatformEntity socialMediaPlatformDtoToSocialMediaPlatformEntity(SocialMediaPlatformDto socialMediaPlatformDto) {
        if (Objects.nonNull(socialMediaPlatformDto)) {
            return SocialMediaPlatformEntity.builder()
                    .id(socialMediaPlatformDto.getId())
                    .createDate(socialMediaPlatformDto.getCreateDate())
                    .createUserId(Objects.nonNull(socialMediaPlatformDto.getCreateUserId()) ? socialMediaPlatformDto.getCreateUserId() : null)
                    .updateDate(Objects.nonNull(socialMediaPlatformDto.getUpdateDate()) ? socialMediaPlatformDto.getUpdateDate(): null)
                    .updateUserId(Objects.nonNull(socialMediaPlatformDto.getUpdateUserId()) ? socialMediaPlatformDto.getUpdateUserId() : null)
                    .status(Status.getIdByStatus(socialMediaPlatformDto.getStatus().getStatus()))
                    .build();
        }
        else {
            throw new RuntimeException("Could not map SocialMediaPlatformDto to SocialMediaPlatformEntity");
        }
    }
}
