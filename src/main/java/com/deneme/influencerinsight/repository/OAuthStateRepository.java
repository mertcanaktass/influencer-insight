package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.OAuthStateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;
import com.deneme.influencerinsight.model.UserEntity;

public interface OAuthStateRepository extends JpaRepository<OAuthStateEntity, Long> {

    Optional<OAuthStateEntity> findByState(String state);

    void deleteByExpiresAtBefore(Instant expirationTime);

    void deleteByUser(UserEntity user);
}
