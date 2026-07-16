package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.repository.SocialMediaSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/** Ham sosyal medya verisinin veri saklama süresini sınırlar. */
@Service
@RequiredArgsConstructor
public class SocialMediaSnapshotMaintenanceService {

    private final SocialMediaSnapshotRepository socialMediaSnapshotRepository;

    @Value("${app.social.snapshot.retention-days:90}")
    private long retentionDays;

    @Scheduled(fixedDelayString = "${app.maintenance.cleanup-delay-ms:3600000}")
    @Transactional
    public void deleteExpiredSnapshots() {
        socialMediaSnapshotRepository.deleteByCollectedAtBefore(
                Instant.now().minus(retentionDays, ChronoUnit.DAYS)
        );
    }
}
