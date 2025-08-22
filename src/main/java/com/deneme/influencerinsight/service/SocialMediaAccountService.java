package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.rest.requests.SocialMediaAccountRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;

import java.util.List;

public interface SocialMediaAccountService {
    SocialMediaAccountResponse addAccount(String username, SocialMediaAccountRequest request);

    List<SocialMediaAccountResponse> getAccounts(String username);

    void deleteAccount(String username, Long accountId);

    SocialMediaAccountResponse syncAccount(String username, Long accountId);

    List<SocialMediaAccountResponse> syncAll(String username);
}
