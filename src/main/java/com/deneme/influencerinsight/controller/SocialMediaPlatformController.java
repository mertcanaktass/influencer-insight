package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.dto.SocialMediaPlatformDto;
import com.deneme.influencerinsight.enums.OperationType;
import com.deneme.influencerinsight.rest.requests.SocialMediaPlatformRequest;
import com.deneme.influencerinsight.rest.responses.AbstractResponse;
import com.deneme.influencerinsight.service.SocialMediaPlatformService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/social-media")
@RequiredArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SocialMediaPlatformController {

    private SocialMediaPlatformService socialMediaService;

    @PostMapping("/createNewPlatform")
    public ResponseEntity<AbstractResponse<SocialMediaPlatformDto>> createSocialMediaPlatform(@RequestBody SocialMediaPlatformRequest socialMediaPlatform) {
        try {
            SocialMediaPlatformDto socialMediaPlatformDto = socialMediaService.createSocialMediaPlatform(socialMediaPlatform);
            AbstractResponse<SocialMediaPlatformDto> response = new AbstractResponse<>();
            response.setOperationType(OperationType.CREATE_SOCIAL_MEDIA_PLATFORM);
            response.setData(socialMediaPlatformDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updatePlatform")
    public ResponseEntity<AbstractResponse<SocialMediaPlatformDto>> updateSocialMediaPlatform(@RequestBody SocialMediaPlatformRequest socialMediaPlatform) {
        try {
            SocialMediaPlatformDto socialMediaPlatformDto = socialMediaService.updateSocialMediaPlatform(socialMediaPlatform);
            AbstractResponse<SocialMediaPlatformDto> response = new AbstractResponse<>();
            response.setOperationType(OperationType.UPDATE_SOCIAL_MEDIA_PLATFORM);
            response.setData(socialMediaPlatformDto);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/deactivatePlatform")
    public ResponseEntity<AbstractResponse<SocialMediaPlatformDto>> deactivateSocialMediaPlatform(@RequestBody SocialMediaPlatformRequest socialMediaPlatform) {
        try {
            SocialMediaPlatformDto socialMediaPlatformDto = socialMediaService.deactivateSocialMediaPlatform(socialMediaPlatform);
            AbstractResponse<SocialMediaPlatformDto> response = new AbstractResponse<>();
            response.setOperationType(OperationType.DEACTIVATE_SOCIAL_MEDIA_PLATFORM);
            response.setData(socialMediaPlatformDto);
            if (socialMediaPlatformDto == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
