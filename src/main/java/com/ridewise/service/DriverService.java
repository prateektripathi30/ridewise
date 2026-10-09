package com.ridewise.service;

import com.ridewise.model.Driver;
import com.ridewise.model.Location;
import com.ridewise.model.VehicleType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class DriverService {
    private final Map<String, Driver> drivers = new LinkedHashMap<>();
    private long nextId = 1;

    public Driver registerDriver(String name, Location location, VehicleType vehicleType) {
        Driver driver = new Driver(
                "D-" + nextId++,
                name,
                Objects.requireNonNull(location, "Location is required."),
                Objects.requireNonNull(vehicleType, "Vehicle type is required."));
        drivers.put(driver.getId(), driver);
        return driver;
    }

    public void updateAvailability(String driverId, boolean available) {
        getDriverById(driverId).setAvailable(available);
    }

    public List<Driver> listAvailableDrivers() {
        return drivers.values().stream().filter(Driver::isAvailable).toList();
    }

    public Driver getDriverById(String driverId) {
        Driver driver = drivers.get(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver not found: " + driverId);
        }
        return driver;
    }

    public void assignDriver(String driverId) {
        getDriverById(driverId).assignRide();
    }

    public void releaseDriver(String driverId) {
        getDriverById(driverId).completeRide();
    }
}
