package com.deneme.influencerinsight.service;

import com.deneme.influencerinsight.exception.RateLimitExceededException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AiRateLimitService {

    private final ConcurrentMap<String, RateLimitWindow> requestWindows = new ConcurrentHashMap<>();
    private final int maxRequests;
    private final long windowSeconds;

    public AiRateLimitService(
            @Value("${app.ai.analysis.max-requests-per-window:10}") int maxRequests,
            @Value("${app.ai.analysis.window-seconds:3600}") long windowSeconds) {
        this.maxRequests = maxRequests;
        this.windowSeconds = windowSeconds;
    }

    public void checkLimit(String username) {
        Instant now = Instant.now();
        AtomicBoolean limitExceeded = new AtomicBoolean(false);

        requestWindows.compute(username, (ignored, currentWindow) -> {
            if (currentWindow == null || currentWindow.startedAt().plusSeconds(windowSeconds).isBefore(now)) {
                return new RateLimitWindow(now, 1);
            }
            if (currentWindow.requestCount() >= maxRequests) {
                limitExceeded.set(true);
                return currentWindow;
            }
            return new RateLimitWindow(currentWindow.startedAt(), currentWindow.requestCount() + 1);
        });

        if (limitExceeded.get()) {
            throw new RateLimitExceededException();
        }
    }

    private record RateLimitWindow(Instant startedAt, int requestCount) {
    }
}
