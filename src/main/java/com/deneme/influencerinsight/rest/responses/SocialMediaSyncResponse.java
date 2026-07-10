package com.deneme.influencerinsight.rest.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SocialMediaSyncResponse {

    private SocialMediaAccountResponse account;
    private boolean successful;
    private String errorCode;
    private String message;
}
