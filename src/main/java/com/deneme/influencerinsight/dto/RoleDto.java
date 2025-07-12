package com.deneme.influencerinsight.dto;

import com.deneme.influencerinsight.enums.RoleType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoleDto {
    private Long id;
    private String roleName;
    private RoleType roleType;
    private Integer status;
}
