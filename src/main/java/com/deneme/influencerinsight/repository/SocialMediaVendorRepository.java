package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.social_media.SocialMediaVendorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialMediaVendorRepository extends JpaRepository<SocialMediaVendorsEntity, Long> {




}
