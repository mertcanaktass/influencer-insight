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
@Table(
        name = "social_media_account",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_social_account_owner_platform_external", columnNames = {"user_id", "platform", "external_id"}),
                @UniqueConstraint(name = "uk_social_account_owner_platform_username", columnNames = {"user_id", "platform", "username"})
        },
        indexes = {
                @Index(name = "idx_social_account_user_platform", columnList = "user_id, platform"),
                @Index(name = "idx_social_account_external_id", columnList = "external_id")
        }
)
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

    @Column(columnDefinition = "TEXT")
    private String accessToken;

    @Column(columnDefinition = "TEXT")
    private String refreshToken;

    private Instant tokenExpiresAt;

    private String externalId;

    /** AI istemi oluşturmak için en güncel snapshot'ın yerel önbelleği. */
    @Column(name = "extra_data", columnDefinition = "TEXT")
    private String extraData;

    @Column(name = "last_synced_at")
    private Instant lastSyncedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Version
    private Long version;
}
