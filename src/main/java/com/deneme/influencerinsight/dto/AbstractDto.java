package com.deneme.influencerinsight.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Setter
@Getter
@SuperBuilder
public class AbstractDto {
    private Date createDate;
    private Long createUser;
    private Date updateDate;
    private Long updateUser;
    private String status;
}
