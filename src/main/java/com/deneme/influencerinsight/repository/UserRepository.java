package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByVerificationToken(String token);

    @Modifying
    @Query("""
            update UserEntity user
            set user.verificationToken = null,
                user.verificationTokenExpiresAt = null
            where user.emailVerified = false
              and user.verificationTokenExpiresAt < :expirationTime
            """)
    int clearExpiredVerificationTokens(Instant expirationTime);
}
