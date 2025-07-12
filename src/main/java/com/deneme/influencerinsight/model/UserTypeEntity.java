package com.deneme.influencerinsight.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_type")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTypeEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_type_desc")
    private String userTypeDesc;
}
