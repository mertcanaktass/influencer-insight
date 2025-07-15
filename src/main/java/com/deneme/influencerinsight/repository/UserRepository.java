package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<UserEntity> findByVerificationToken(String token);
}
