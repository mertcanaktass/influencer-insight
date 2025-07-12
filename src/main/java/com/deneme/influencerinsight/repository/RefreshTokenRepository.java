package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.RefreshTokenEntity;
import com.deneme.influencerinsight.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByUser(UserEntity user);
}
