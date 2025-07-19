package com.deneme.influencerinsight.model;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_media_account")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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

    private String accessToken; // İsteğe bağlı, OpenAI erişimi için kullanılabilir

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;
}
