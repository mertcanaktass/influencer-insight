package com.deneme.influencerinsight.dto;

import com.deneme.influencerinsight.enums.RoleType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class RoleDto extends AbstractDto {
    private Long id;
    private String roleName;
    private RoleType roleType;
    private Integer status;
}
