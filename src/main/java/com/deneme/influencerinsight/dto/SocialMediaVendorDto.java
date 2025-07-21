package com.deneme.influencerinsight.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SocialMediaVendorDto extends AbstractDto {
    private Long id;
    private String name;
    private String vendorShortCode;
    private String website;
    private String description;
}
