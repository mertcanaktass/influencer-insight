package com.deneme.influencerinsight.model;

import jakarta.persistence.Column;

import java.util.Date;

public abstract class AbstractEntity {

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

}
