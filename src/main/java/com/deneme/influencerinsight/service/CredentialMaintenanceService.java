package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class CredentialMaintenanceService {

    private final UserRepository userRepository;

    @Scheduled(fixedDelayString = "${app.maintenance.cleanup-delay-ms:3600000}")
    @Transactional
    public void clearExpiredVerificationTokens() {
        userRepository.clearExpiredVerificationTokens(Instant.now());
    }
}
