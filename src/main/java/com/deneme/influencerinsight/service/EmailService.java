package com.deneme.influencerinsight.service;

public interface EmailService {
    void sendVerificationEmail(String to, String token);
}
