package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.model.SocialMediaAccountEntity;
import com.deneme.influencerinsight.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SocialMediaAccountRepository extends JpaRepository<SocialMediaAccountEntity, Long> {

    List<SocialMediaAccountEntity> findAllByUser(UserEntity user);

    Optional<SocialMediaAccountEntity> findByIdAndUser(Long id, UserEntity user);

    List<SocialMediaAccountEntity> findAllByUserUsername(String username);

    List<SocialMediaAccountEntity> findAllByUserUsernameAndPlatform(String username, SocialMediaPlatform platform);
}
