package com.deneme.influencerinsight.repository;

import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
    RoleEntity findByRoleType(RoleType roleType);
}
