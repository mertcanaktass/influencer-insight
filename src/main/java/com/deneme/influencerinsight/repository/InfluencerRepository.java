package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.model.Influencer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InfluencerRepository extends JpaRepository<Influencer, Long> {

    boolean existsByEmail(String email);
}
