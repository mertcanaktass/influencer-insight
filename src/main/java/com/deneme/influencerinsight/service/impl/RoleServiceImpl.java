package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.repository.RoleRepository;
import com.deneme.influencerinsight.service.RoleService;
import org.springframework.stereotype.Service;

import static com.deneme.influencerinsight.mapper.RoleMapper.entityToRole;

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleDto getRoleById(Long roleId) {
        return null;
    }

    @Override
    public RoleDto getRoleByType(RoleType roleType) {
        RoleEntity roleEntity = this.roleRepository.findByRoleType(roleType);
        return entityToRole(roleEntity);
    }
}
