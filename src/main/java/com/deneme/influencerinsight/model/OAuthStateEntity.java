package com.deneme.influencerinsight.model;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "oauth_state", indexes = @Index(name = "idx_oauth_state_expires_at", columnList = "expires_at"))
@Getter
@Setter
@NoArgsConstructor
public class OAuthStateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String state;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialMediaPlatform platform;

    @Column(nullable = false)
    private Instant expiresAt;
}
