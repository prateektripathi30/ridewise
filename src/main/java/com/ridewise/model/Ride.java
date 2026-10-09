package com.ridewise.model;

import java.time.Instant;
import java.util.Objects;

public final class Ride {
    private final String id;
    private final Rider rider;
    private final VehicleType vehicleType;
    private final double distance;
    private final Instant requestedAt;
    private Driver driver;
    private RideStatus status = RideStatus.REQUESTED;

    public Ride(String id, Rider rider, VehicleType vehicleType, double distance) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Ride ID is required.");
        }
        if (!Double.isFinite(distance) || distance <= 0) {
            throw new IllegalArgumentException("Ride distance must be a positive finite number.");
        }
        this.id = id.trim();
        this.rider = Objects.requireNonNull(rider, "Rider is required.");
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type is required.");
        this.distance = distance;
        this.requestedAt = Instant.now();
    }

    public void assignTo(Driver driver) {
        requireStatus(RideStatus.REQUESTED);
        this.driver = Objects.requireNonNull(driver, "Driver is required.");
        status = RideStatus.ASSIGNED;
    }

    public void complete() {
        requireStatus(RideStatus.ASSIGNED);
        status = RideStatus.COMPLETED;
    }

    public void cancel() {
        if (status != RideStatus.REQUESTED && status != RideStatus.ASSIGNED) {
            throw new IllegalStateException("Only requested or assigned rides can be cancelled.");
        }
        status = RideStatus.CANCELLED;
    }

    private void requireStatus(RideStatus expected) {
        if (status != expected) {
            throw new IllegalStateException("Ride " + id + " must be " + expected + " to perform this action.");
        }
    }

    public String getId() {
        return id;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public double getDistance() {
        return distance;
    }

    public RideStatus getStatus() {
        return status;
    }

    public Instant getRequestedAt() {
        return requestedAt;
    }
}
