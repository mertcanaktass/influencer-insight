package com.deneme.influencerinsight.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaPlatformDto extends AbstractDto {

    private Long id;
    private String name;
    private String vendorName;
    private String platformName;
    private String platformShortCode;

}
