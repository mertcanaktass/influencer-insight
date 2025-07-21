package com.deneme.influencerinsight.dto;

import com.deneme.influencerinsight.enums.Status;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

import java.util.Date;

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
