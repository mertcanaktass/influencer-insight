package com.deneme.influencerinsight.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@Table(name = "token_blacklist", indexes = @Index(name = "idx_blacklist_expiration", columnList = "expiration_date"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TokenBlacklistEntity extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", nullable = false, unique = true, length = 64)
    private String tokenHash;

    @Column(name = "expiration_date", nullable = false)
    private Date expirationDate;

}
