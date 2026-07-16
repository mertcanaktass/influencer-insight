package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.TokenBlacklistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklistEntity, Long> {
    boolean existsByTokenHash(String tokenHash);

    void deleteByExpirationDateBefore(Date expirationDate);
}
