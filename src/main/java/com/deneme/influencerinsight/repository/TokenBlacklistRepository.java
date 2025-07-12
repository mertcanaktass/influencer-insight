package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Long> {
    boolean existsByToken(String token);
}
