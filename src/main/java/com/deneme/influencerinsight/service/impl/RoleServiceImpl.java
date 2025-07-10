package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.repository.RoleRepository;
import com.deneme.influencerinsight.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

  @Autowired
  private RoleRepository roleRepository;

  @Override
  public RoleEntity getRoleById(Long roleId) {
    return null;
  }

  @Override
  public Optional<RoleEntity> getRoleByType(RoleType roleType) {
    return this.roleRepository.findByRoleType(roleType);
  }


}
