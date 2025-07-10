package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;

import java.util.Optional;

public interface RoleService {

  RoleEntity getRoleById(Long roleId);

  Optional<RoleEntity> getRoleByType(RoleType roleType);


}
