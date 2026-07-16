package com.deneme.influencerinsight.exception;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException() {
        super("Analysis request limit exceeded.");
    }
}
