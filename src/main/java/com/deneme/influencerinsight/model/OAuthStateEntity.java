package com.deneme.influencerinsight.model;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "oauth_state")
@Getter
@Setter
@NoArgsConstructor
public class OAuthStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String state;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialMediaPlatform platform;

    @Column(nullable = false)
    private Instant expiresAt;
}
