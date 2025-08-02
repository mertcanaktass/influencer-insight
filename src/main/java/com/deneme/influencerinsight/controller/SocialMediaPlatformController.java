package com.deneme.influencerinsight.controller;

import com.deneme.influencerinsight.dto.SocialMediaPlatformDto;
import com.deneme.influencerinsight.enums.OperationType;
import com.deneme.influencerinsight.rest.requests.SocialMediaPlatformRequest;
import com.deneme.influencerinsight.rest.responses.AbstractResponse;
import com.deneme.influencerinsight.service.SocialMediaPlatformService;
import io.swagger.v3.oas.annotations.Operation;
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
    @Operation(
            summary = "Create Social Media Platform",
            description = "Creates Social Media Platform record for connect Costomers Social Media Account",
            tags = {"Social Media Platform Manager"}
    )
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
    @Operation(
            summary = "Update Social Media Platform",
            description = "Updates Social Media Platform specifications due to changes for that platform",
            tags = {"Social Media Platform Manager"}
    )
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
    @Operation(
            summary = "Deactivate Social Media Platform",
            description = "Deactivates Social Media Platform record if needed",
            tags = {"Social Media Platform Manager"}
    )
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
