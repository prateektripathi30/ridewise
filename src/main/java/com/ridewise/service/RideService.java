package com.ridewise.service;

import com.ridewise.model.Driver;
import com.ridewise.model.FareReceipt;
import com.ridewise.model.Ride;
import com.ridewise.model.RideStatus;
import com.ridewise.model.Rider;
import com.ridewise.model.VehicleType;
import com.ridewise.strategy.FareStrategy;
import com.ridewise.strategy.RideMatchingStrategy;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public final class RideService {
    private final RiderService riderService;
    private final DriverService driverService;
    private final RideMatchingStrategy rideMatchingStrategy;
    private final FareStrategy fareStrategy;
    private final Map<String, Ride> rides = new LinkedHashMap<>();
    private final Map<String, FareReceipt> fareReceipts = new LinkedHashMap<>();
    private long nextId = 1;

    public RideService(
            RiderService riderService,
            DriverService driverService,
            RideMatchingStrategy rideMatchingStrategy,
            FareStrategy fareStrategy) {
        this.riderService = Objects.requireNonNull(riderService, "Rider service is required.");
        this.driverService = Objects.requireNonNull(driverService, "Driver service is required.");
        this.rideMatchingStrategy = Objects.requireNonNull(rideMatchingStrategy, "Matching strategy is required.");
        this.fareStrategy = Objects.requireNonNull(fareStrategy, "Fare strategy is required.");
    }

    public Ride requestRide(String riderId, double distance, VehicleType vehicleType) {
        Rider rider = riderService.getRiderById(riderId);
        Ride ride = new Ride("RIDE-" + nextId++, rider, vehicleType, distance);

        List<Driver> eligibleDrivers = driverService.listAvailableDrivers().stream()
                .filter(driver -> driver.getVehicleType() == vehicleType)
                .toList();
        Driver selectedDriver = rideMatchingStrategy.findDriver(rider, eligibleDrivers);
        if (selectedDriver != null) {
            boolean eligible = eligibleDrivers.stream()
                    .anyMatch(driver -> driver == selectedDriver);
            if (!eligible) {
                throw new IllegalStateException("Matching strategy selected a driver outside the eligible list.");
            }
            driverService.assignDriver(selectedDriver.getId());
            ride.assignTo(selectedDriver);
        }
        rides.put(ride.getId(), ride);
        return ride;
    }

    public FareReceipt completeRide(String rideId) {
        Ride ride = getRideById(rideId);
        if (ride.getStatus() != RideStatus.ASSIGNED) {
            throw new IllegalStateException("Only assigned rides can be completed.");
        }
        double amount = fareStrategy.calculateFare(ride);
        if (!Double.isFinite(amount) || amount < 0) {
            throw new IllegalStateException("Fare strategy returned an invalid fare.");
        }

        FareReceipt receipt = new FareReceipt(ride.getId(), amount, Instant.now());
        Driver driver = ride.getDriver();
        ride.complete();
        driverService.releaseDriver(driver.getId());
        fareReceipts.put(ride.getId(), receipt);
        return receipt;
    }

    public void cancelRide(String rideId) {
        Ride ride = getRideById(rideId);
        if (ride.getStatus() == RideStatus.ASSIGNED) {
            driverService.releaseDriver(ride.getDriver().getId());
        }
        ride.cancel();
    }

    public Ride getRideById(String rideId) {
        Ride ride = rides.get(rideId);
        if (ride == null) {
            throw new IllegalArgumentException("Ride not found: " + rideId);
        }
        return ride;
    }

    public List<Ride> listRides() {
        return List.copyOf(rides.values());
    }

    public FareReceipt getFareReceipt(String rideId) {
        FareReceipt receipt = fareReceipts.get(rideId);
        if (receipt == null) {
            throw new IllegalArgumentException("Fare receipt not found for ride: " + rideId);
        }
        return receipt;
    }

    public List<Ride> listRidesForRider(String riderId) {
        riderService.getRiderById(riderId);
        return rides.values().stream()
                .filter(ride -> ride.getRider().getId().equals(riderId))
                .collect(Collectors.toUnmodifiableList());
    }
}
