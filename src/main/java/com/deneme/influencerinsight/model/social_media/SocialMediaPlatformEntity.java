//package com.deneme.influencerinsight.model.social_media;
//
//import com.deneme.influencerinsight.model.AbstractEntity;
//import jakarta.persistence.*;
//import lombok.*;
//import lombok.experimental.SuperBuilder;
//
//@Entity
//@Table(name = "social_media_platform")
//@Getter
//@Setter
//@SuperBuilder
//@AllArgsConstructor
//@NoArgsConstructor
//@MappedSuperclass
//public class SocialMediaPlatformEntity extends AbstractEntity {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(name = "name", nullable = false)
//    private String platformName;
//
//    @Column(name = "shrt_code", nullable = false)
//    private String shortCode;
//
//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "vendor_id", nullable = false, insertable = false, updatable = false)
//    private SocialMediaVendorsEntity socialMediaVendor;
//
//}
