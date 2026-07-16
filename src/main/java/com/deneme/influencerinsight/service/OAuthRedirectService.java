package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.enums.SocialMediaPlatform;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Locale;

/** OAuth callback sonucunu güvenilir frontend hesabım sayfasına yönlendirir. */
@Service
public class OAuthRedirectService {

    private final String frontendBaseUrl;

    public OAuthRedirectService(@Value("${app.frontend.base-url:http://localhost:3000}") String frontendBaseUrl) {
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public void redirectToAccounts(HttpServletResponse response,
                                   SocialMediaPlatform platform,
                                   boolean successful) throws IOException {
        String redirectUrl = UriComponentsBuilder.fromUriString(frontendBaseUrl)
                .path("/accounts")
                .queryParam("oauth", platform.name().toLowerCase(Locale.ROOT))
                .queryParam("status", successful ? "success" : "error")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }
}
