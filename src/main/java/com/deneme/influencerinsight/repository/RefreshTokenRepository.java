package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.RefreshTokenEntity;
import com.deneme.influencerinsight.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.time.Instant;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByTokenHash(String tokenHash);

    void deleteByUser(UserEntity user);

    void deleteByExpiryDateBefore(Instant expirationTime);
}
