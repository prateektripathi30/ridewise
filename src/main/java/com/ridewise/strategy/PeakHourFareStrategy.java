package com.ridewise.strategy;

import com.ridewise.model.Ride;

import java.time.Clock;
import java.time.LocalTime;
import java.util.Objects;

public final class PeakHourFareStrategy implements FareStrategy {
    private final FareStrategy baseStrategy;
    private final Clock clock;
    private final double multiplier;

    public PeakHourFareStrategy(FareStrategy baseStrategy, Clock clock, double multiplier) {
        this.baseStrategy = Objects.requireNonNull(baseStrategy, "Base fare strategy is required.");
        this.clock = Objects.requireNonNull(clock, "Clock is required.");
        if (!Double.isFinite(multiplier) || multiplier < 1) {
            throw new IllegalArgumentException("Peak multiplier must be a finite number of at least 1.");
        }
        this.multiplier = multiplier;
    }

    @Override
    public double calculateFare(Ride ride) {
        double baseFare = baseStrategy.calculateFare(ride);
        LocalTime now = LocalTime.now(clock);
        boolean morningPeak = !now.isBefore(LocalTime.of(7, 0)) && now.isBefore(LocalTime.of(10, 0));
        boolean eveningPeak = !now.isBefore(LocalTime.of(17, 0)) && now.isBefore(LocalTime.of(20, 0));
        return morningPeak || eveningPeak ? baseFare * multiplier : baseFare;
    }
}
