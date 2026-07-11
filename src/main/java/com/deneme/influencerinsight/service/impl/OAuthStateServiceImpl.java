package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.OAuthStateEntity;
import com.deneme.influencerinsight.repository.OAuthStateRepository;
import com.deneme.influencerinsight.service.OAuthStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class OAuthStateServiceImpl implements OAuthStateService {

    private static final int STATE_BYTE_LENGTH = 32;

    private final OAuthStateRepository oauthStateRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.oauth.state-ttl-seconds:600}")
    private long stateTtlSeconds;

    @Override
    @Transactional
    public String createState(String username, SocialMediaPlatform platform) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("OAuth state requires an authenticated user.");
        }

        Instant now = Instant.now();
        oauthStateRepository.deleteByExpiresAtBefore(now);

        OAuthStateEntity oauthState = new OAuthStateEntity();
        oauthState.setState(generateState());
        oauthState.setUsername(username);
        oauthState.setPlatform(platform);
        oauthState.setExpiresAt(now.plusSeconds(stateTtlSeconds));

        return oauthStateRepository.save(oauthState).getState();
    }

    @Override
    @Transactional
    public String consumeState(String state, SocialMediaPlatform platform) {
        OAuthStateEntity oauthState = oauthStateRepository.findByState(state)
                .orElseThrow(() -> new IllegalArgumentException("Invalid OAuth state."));

        oauthStateRepository.delete(oauthState);

        if (oauthState.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Expired OAuth state.");
        }
        if (oauthState.getPlatform() != platform) {
            throw new IllegalArgumentException("OAuth state does not match the requested platform.");
        }

        return oauthState.getUsername();
    }

    private String generateState() {
        byte[] bytes = new byte[STATE_BYTE_LENGTH];
        secureRandom.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Scheduled(fixedDelayString = "${app.maintenance.cleanup-delay-ms:3600000}")
    @Transactional
    public void deleteExpiredStates() {
        oauthStateRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
