package com.deneme.influencerinsight.social.provider;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import com.deneme.influencerinsight.social.SocialPlatformProvider;
import org.springframework.stereotype.Component;

@Component
public class InstagramProvider implements SocialPlatformProvider {
    @Override
    public boolean supports(SocialMediaPlatform platform) {
        return platform == SocialMediaPlatform.INSTAGRAM;
    }

    @Override
    public String fetchAccountSnapshotJson(String username, String accessToken) {
        // TODO: Gerçek Instagram Graph API çağrısı ile değiştirilecek
        return """
                {
                  "platform": "INSTAGRAM",
                  "username": "%s",
                  "followers": 8421,
                  "avgLikes": 356,
                  "recentPosts": [
                    {"id":"p1","caption":"Hello","likes": 400},
                    {"id":"p2","caption":"Reel","likes": 310}
                  ]
                }
                """.formatted(username);
    }
}
