//package com.deneme.influencerinsight.service.impl;
//
//import com.deneme.influencerinsight.dto.SocialMediaPlatformDto;
//import com.deneme.influencerinsight.enums.Status;
//import com.deneme.influencerinsight.mapper.SocialMediaPlatformMapper;
//import com.deneme.influencerinsight.model.social_media.SocialMediaPlatformEntity;
//import com.deneme.influencerinsight.repository.SocialMediaPlatformRepository;
//import com.deneme.influencerinsight.rest.requests.SocialMediaPlatformRequest;
//import com.deneme.influencerinsight.service.SocialMediaPlatformService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//
//import java.util.Date;
//import java.util.Objects;
//
///***
// * @author Osman Kutay Yağcı
// * @since 0.0.1 SNAPSHOT
// * @version 0.0.1 SNAPSHOT
// * @see com.deneme.influencerinsight.service.SocialMediaPlatformService
// * @Description: Social Media Platform Service for create, update, deactivate and get social media platforms
// */
//@Service
//@RequiredArgsConstructor
//public class SocialMediaPlatformServiceImpl implements SocialMediaPlatformService {
//
//    private final SocialMediaPlatformRepository socialMediaPlatformRepository;
//    private final SocialMediaPlatformMapper socialMediaPlatformMapper;
//
//    /**
//     * Gets a social media platform by its short code.
//     * @param shortCode the short code of the social media platform
//     * @return the social media platform
//     * @throws RuntimeException if the social media platform does not exist
//     * @Description: Get a Social Media Platform Record from Database by Short Code
//     * @Author: Osman Kutay Yağcı
//     */
//    @Override
//    public SocialMediaPlatformDto getSocialMediaPlatform(String shortCode) {
//        SocialMediaPlatformEntity socialMediaPlatformEntity = socialMediaPlatformRepository.findByShortCode(shortCode);
//        if (socialMediaPlatformEntity != null) {
//            return socialMediaPlatformMapper.socialMediaPlatformEntityToSocialMediaPlatformDto(socialMediaPlatformEntity);
//        } else {
//            throw new RuntimeException("Social Media Platform not found");
//        }
//    }
//
//    /**
//     * Create a new social media platform.
//     *
//     * @param request the request object for creating a new social media platform
//     * @return the created social media platform
//     * @Description: Create a new Social Media Platform Record to Database
//     * @Author: Osman Kutay Yağcı
//     */
//    @Override
//    public SocialMediaPlatformDto createSocialMediaPlatform(SocialMediaPlatformRequest request) {
//        try {
//            SocialMediaPlatformDto socialMediaPlatformDto = SocialMediaPlatformDto.builder()
//                    .name(request.getName())
//                    .vendorName(request.getVendorName())
//                    .createDate(request.getCreateDate())
//                    .createUserId(Objects.nonNull(request.getCreateUserId()) ? request.getCreateUserId() : null)
//                    .updateUserId(null)
//                    .updateDate(null)
//                    .status(Status.getStatusByShortCode(request.getStatus()))
//                    .build();
//            SocialMediaPlatformEntity socialMediaPlatformEntity = socialMediaPlatformMapper.socialMediaPlatformDtoToSocialMediaPlatformEntity(socialMediaPlatformDto);
//            socialMediaPlatformEntity = socialMediaPlatformRepository.save(socialMediaPlatformEntity);
//            return socialMediaPlatformMapper.socialMediaPlatformEntityToSocialMediaPlatformDto(socialMediaPlatformEntity);
//        } catch (RuntimeException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    /**
//     * Update an existing social media platform.
//     *
//     * @param request the request object for updating a social media platform
//     * @return the updated social media platform
//     * @Description: Update an existing Social Media Platform Record to Database
//     * @Author: Osman Kutay Yağcı
//     */
//    @Override
//    public SocialMediaPlatformDto updateSocialMediaPlatform(SocialMediaPlatformRequest request) {
//        SocialMediaPlatformEntity socialMediaPlatformEntity = socialMediaPlatformRepository.findByShortCode(request.getPlatformShortCode());
//        if (socialMediaPlatformEntity != null) {
//            SocialMediaPlatformDto socialMediaPlatformDto = socialMediaPlatformMapper.socialMediaPlatformEntityToSocialMediaPlatformDto(socialMediaPlatformEntity);
//            socialMediaPlatformDto.setVendorName(request.getVendorName());
//            socialMediaPlatformDto.setStatus(Status.getStatusByShortCode(request.getStatus()));
//            socialMediaPlatformDto.setUpdateDate(new Date());
//            socialMediaPlatformDto.setUpdateUserId(Objects.nonNull(request.getUpdateUserId()) ? request.getUpdateUserId() : null);
//            socialMediaPlatformEntity = socialMediaPlatformMapper.socialMediaPlatformDtoToSocialMediaPlatformEntity(socialMediaPlatformDto);
//            socialMediaPlatformEntity = socialMediaPlatformRepository.save(socialMediaPlatformEntity);
//            return socialMediaPlatformMapper.socialMediaPlatformEntityToSocialMediaPlatformDto(socialMediaPlatformEntity);
//        } else {
//            throw new RuntimeException("Social Media Platform not found");
//        }
//    }
//
//
//    /**
//     * Deactivate an existing social media platform.
//     *
//     * @param request the request object for deactivating a social media platform
//     * @return the deactivated social media platform
//     * @Description: Deactivate an existing Social Media Platform Record to Database
//     * @Author: Osman Kutay Yağcı
//     */
//    @Override
//    public SocialMediaPlatformDto deactivateSocialMediaPlatform(SocialMediaPlatformRequest request) {
//        SocialMediaPlatformEntity socialMediaPlatformEntity = socialMediaPlatformRepository.findByShortCode(request.getPlatformShortCode());
//        if (socialMediaPlatformEntity != null) {
//            socialMediaPlatformEntity.setStatus(Status.getStatusByShortCode(request.getStatus()).getId());
//            socialMediaPlatformEntity.setUpdateDate(new Date());
//            socialMediaPlatformEntity.setUpdateUserId(Objects.nonNull(request.getUpdateUserId()) ? request.getUpdateUserId() : null);
//            socialMediaPlatformEntity = socialMediaPlatformRepository.save(socialMediaPlatformEntity);
//            return socialMediaPlatformMapper.socialMediaPlatformEntityToSocialMediaPlatformDto(socialMediaPlatformEntity);
//        } else {
//            throw new RuntimeException("Social Media Platform not found");
//        }
//    }
//}
