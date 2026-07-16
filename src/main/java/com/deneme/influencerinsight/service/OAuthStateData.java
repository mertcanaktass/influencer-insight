package com.deneme.influencerinsight.service;

/** OAuth callback'i tamamlamak için tek kullanımlık state ile saklanan veriler. */
public record OAuthStateData(String username, String codeVerifier) {
}
