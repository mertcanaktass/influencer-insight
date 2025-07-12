package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.dto.RoleDto;
import com.deneme.influencerinsight.enums.RoleType;

public interface RoleService {

    RoleDto getRoleById(Long roleId);

    RoleDto getRoleByType(RoleType roleType);
}
