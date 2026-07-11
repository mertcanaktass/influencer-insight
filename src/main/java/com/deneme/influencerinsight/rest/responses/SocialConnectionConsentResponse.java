package com.deneme.influencerinsight.rest.responses;

import java.time.Instant;

public record SocialConnectionConsentResponse(boolean accepted, String policyVersion, Instant acceptedAt) {
}
