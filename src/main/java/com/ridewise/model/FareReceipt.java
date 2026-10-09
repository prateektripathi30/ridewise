package com.ridewise.model;

import java.time.Instant;
import java.util.Objects;

public record FareReceipt(String rideId, double amount, Instant generatedAt) {
    public FareReceipt {
        if (rideId == null || rideId.isBlank()) {
            throw new IllegalArgumentException("Ride ID is required.");
        }
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Fare must be a non-negative finite number.");
        }
        rideId = rideId.trim();
        generatedAt = Objects.requireNonNull(generatedAt, "Receipt generation time is required.");
    }
}
