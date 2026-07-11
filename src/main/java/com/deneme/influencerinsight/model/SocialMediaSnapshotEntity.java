package com.deneme.influencerinsight.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/** Platformdan çekilen, zaman damgalı ham hesap verisi. */
@Entity
@Table(name = "social_media_snapshot", indexes = @Index(
        name = "idx_social_snapshot_account_collected",
        columnList = "account_id, collected_at"
))
@Getter
@Setter
@NoArgsConstructor
public class SocialMediaSnapshotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private SocialMediaAccountEntity account;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(name = "schema_version", nullable = false)
    private Integer schemaVersion = 1;
}
