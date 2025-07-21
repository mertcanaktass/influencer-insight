package com.deneme.influencerinsight.rest.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SocialMediaPlatformRequest {

    private String name;
    private String platformShortCode;
    private String vendorName;
    private String status;
    private Date createDate;
    private Long createUserId;
    private Date updateDate;
    private Long updateUserId;

}
