package com.deneme.influencerinsight.model;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.Instant;

@Entity
@Table(name = "social_media_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SocialMediaAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SocialMediaPlatform platform;

    @Column(nullable = false)
    private String username;

    private String profileUrl;

    private String accessToken;

    private String refreshToken;

    private Instant tokenExpiresAt;

    private String externalId;

    @Lob
    @Basic(fetch = FetchType.EAGER)
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;   // platformdan gelen detaylı analiz için JSON verisi

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
