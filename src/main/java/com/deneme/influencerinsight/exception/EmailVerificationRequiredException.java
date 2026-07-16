package com.deneme.influencerinsight.exception;

/** Kimlik bilgileri doğru ancak e-posta doğrulaması tamamlanmamış kullanıcıyı belirtir. */
public class EmailVerificationRequiredException extends RuntimeException {

    public EmailVerificationRequiredException() {
        super("Email verification is required.");
    }
}
