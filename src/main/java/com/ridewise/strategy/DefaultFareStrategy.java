package com.ridewise.strategy;

import com.ridewise.model.Ride;

import java.util.Objects;

public final class DefaultFareStrategy implements FareStrategy {
    @Override
    public double calculateFare(Ride ride) {
        Objects.requireNonNull(ride, "Ride is required.");
        return ride.getDistance() * ride.getVehicleType().getFarePerKilometre();
    }
}
