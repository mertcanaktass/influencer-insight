package com.deneme.influencerinsight.model;

import jakarta.persistence.*;

@Entity
@Table(name = "user_role")
public class UserRoleEntity extends AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;


}
