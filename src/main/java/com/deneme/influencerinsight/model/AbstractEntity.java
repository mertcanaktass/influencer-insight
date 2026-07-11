package com.deneme.influencerinsight.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractEntity {

    @Column(name = "create_user_id")
    private Long createUserId;

    @Column(name = "update_user_id")
    private Long updateUserId;

    @Column(name = "create_date", nullable = false)
    private Date createDate;

    @Column(name = "update_date")
    private Date updateDate;

    @PrePersist
    protected void onCreate() {
        if (createDate == null) {
            createDate = new Date();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updateDate = new Date();
    }

}
