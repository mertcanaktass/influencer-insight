package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.exception.ResourceNotFoundException;
import com.deneme.influencerinsight.mapper.SocialMediaAccountMapper;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.rest.requests.SocialMediaAccountRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;
import com.deneme.influencerinsight.rest.responses.UserResponse;
import com.deneme.influencerinsight.service.SocialMediaAccountService;
import com.deneme.influencerinsight.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.deneme.influencerinsight.mapper.UserMapper.userResponseToEntity;

@Service
@RequiredArgsConstructor
public class SocialMediaAccountServiceImpl implements SocialMediaAccountService {

    private final SocialMediaAccountRepository socialMediaAccountRepository;
    private final UserService userService;

    @Override
    public SocialMediaAccountResponse addAccount(String username, SocialMediaAccountRequest request) {
        UserResponse user = userService.inquireUserWithUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        SocialMediaAccountEntity account = SocialMediaAccountMapper.requestToEntity(request);
        account.setUser(userResponseToEntity(user));
        SocialMediaAccountEntity saved = socialMediaAccountRepository.save(account);

        return SocialMediaAccountMapper.entityToResponse(saved);
    }

    @Override
    public List<SocialMediaAccountResponse> getAccounts(String username) {
        UserResponse user = userService.inquireUserWithUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return socialMediaAccountRepository.findAllByUser(userResponseToEntity(user)).stream()
                .map(SocialMediaAccountMapper::entityToResponse)
                .toList();
    }

    @Override
    public void deleteAccount(String username, Long accountId) {
        UserResponse user = userService.inquireUserWithUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        SocialMediaAccountEntity account = socialMediaAccountRepository.findByIdAndUser(accountId, userResponseToEntity(user))
                .orElseThrow(() -> new ResourceNotFoundException("Social media account not found"));

        socialMediaAccountRepository.delete(account);
    }

}
