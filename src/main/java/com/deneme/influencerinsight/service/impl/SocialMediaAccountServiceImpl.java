package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.exception.ResourceNotFoundException;
import com.deneme.influencerinsight.mapper.SocialMediaAccountMapper;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.model.SocialMediaSnapshotEntity;
import com.deneme.influencerinsight.repository.SocialMediaAccountRepository;
import com.deneme.influencerinsight.repository.SocialMediaSnapshotRepository;
import com.deneme.influencerinsight.rest.requests.SocialMediaAccountRequest;
import com.deneme.influencerinsight.rest.responses.SocialMediaAccountResponse;
import com.deneme.influencerinsight.rest.responses.SocialMediaSyncResponse;
import com.deneme.influencerinsight.model.UserEntity;
import com.deneme.influencerinsight.service.SocialMediaAccountService;
import com.deneme.influencerinsight.service.UserService;
import com.deneme.influencerinsight.security.SocialTokenCipher;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import com.deneme.influencerinsight.social.SocialProviderRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialMediaAccountServiceImpl implements SocialMediaAccountService {

    private final SocialMediaAccountRepository socialMediaAccountRepository;
    private final SocialMediaSnapshotRepository socialMediaSnapshotRepository;
    private final UserService userService;
    private final SocialProviderRegistry providerRegistry;
    private final SocialTokenCipher socialTokenCipher;

    @Override
    public SocialMediaAccountResponse addAccount(String username, SocialMediaAccountRequest request) {
        UserEntity user = userService.getRequiredUserByUsername(username);

        SocialMediaAccountEntity account = SocialMediaAccountMapper.requestToEntity(request);
        account.setUser(user);
        account.setAccessToken(socialTokenCipher.encrypt(account.getAccessToken()));

        SocialMediaAccountEntity saved = socialMediaAccountRepository.save(account);
        return SocialMediaAccountMapper.entityToResponse(saved);
    }

    @Transactional
    @Override
    public List<SocialMediaAccountResponse> getAccounts(String username) {
        UserEntity user = userService.getRequiredUserByUsername(username);

        return socialMediaAccountRepository.findAllByUser(user).stream()
                .map(SocialMediaAccountMapper::entityToResponse)
                .toList();
    }

    @Transactional
    @Override
    public void deleteAccount(String username, Long accountId) {
        UserEntity user = userService.getRequiredUserByUsername(username);

        SocialMediaAccountEntity account = socialMediaAccountRepository.findByIdAndUser(accountId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Social media account not found"));

        socialMediaSnapshotRepository.deleteByAccount(account);
        socialMediaAccountRepository.delete(account);
    }

    @Override
    public SocialMediaSyncResponse syncAccount(String username, Long accountId) {
        UserEntity user = userService.getRequiredUserByUsername(username);

        SocialMediaAccountEntity account = socialMediaAccountRepository
                .findByIdAndUser(accountId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Social media account not found"));

        return synchronize(account);
    }

    @Override
    public List<SocialMediaSyncResponse> syncAll(String username) {
        UserEntity user = userService.getRequiredUserByUsername(username);

        List<SocialMediaAccountEntity> accounts = socialMediaAccountRepository.findAllByUser(user);
        return accounts.stream()
                .map(this::synchronize)
                .toList();
    }

    private SocialMediaSyncResponse synchronize(SocialMediaAccountEntity account) {
        try {
            SocialPlatformProvider provider = providerRegistry.getProvider(account.getPlatform());
            String snapshot = provider.fetchAccountSnapshotJson(account);

            Instant collectedAt = Instant.now();
            account.setLastSyncedAt(collectedAt);
            SocialMediaAccountEntity savedAccount = socialMediaAccountRepository.save(account);

            SocialMediaSnapshotEntity snapshotEntity = new SocialMediaSnapshotEntity();
            snapshotEntity.setAccount(savedAccount);
            snapshotEntity.setCollectedAt(collectedAt);
            snapshotEntity.setPayload(snapshot);
            socialMediaSnapshotRepository.save(snapshotEntity);

            return SocialMediaSyncResponse.builder()
                    .account(SocialMediaAccountMapper.entityToResponse(savedAccount))
                    .successful(true)
                    .message("Synchronization completed.")
                    .build();
        } catch (RuntimeException ex) {
            log.warn("Synchronization failed for social media account {}", account.getId(), ex);
            return SocialMediaSyncResponse.builder()
                    .account(SocialMediaAccountMapper.entityToResponse(account))
                    .successful(false)
                    .errorCode("SYNC_FAILED")
                    .message("Synchronization could not be completed.")
                    .build();
        }
    }

}
