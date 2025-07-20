package com.deneme.influencerinsight.service.impl;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.enums.RoleType;
import com.deneme.influencerinsight.exception.ResourceNotFoundException;
import com.deneme.influencerinsight.model.RoleEntity;
import com.deneme.influencerinsight.repository.RoleRepository;
import com.deneme.influencerinsight.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.deneme.influencerinsight.mapper.RoleMapper.entityToRoleDto;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleDto getRoleById(Long roleId) {
        RoleEntity roleEntity = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + roleId));
        return entityToRoleDto(roleEntity);
    }

    @Override
    public RoleDto getRoleByType(RoleType roleType) {
        RoleEntity roleEntity = roleRepository.findById(roleType.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with type: " + roleType.getType()));
        return entityToRoleDto(roleEntity);
    }
}
