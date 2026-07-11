package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.SocialMediaSnapshotEntity;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface SocialMediaSnapshotRepository extends JpaRepository<SocialMediaSnapshotEntity, Long> {
    void deleteByAccount(SocialMediaAccountEntity account);

    void deleteByCollectedAtBefore(Instant cutoff);

    List<SocialMediaSnapshotEntity> findAllByAccountIn(Collection<SocialMediaAccountEntity> accounts);
}
