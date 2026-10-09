package com.ridewise.model;

public final class Driver {
    private final String id;
    private final String name;
    private final VehicleType vehicleType;
    private Location currentLocation;
    private boolean available = true;
    private boolean activeRide;
    private int totalRidesAssigned;

    public Driver(String id, String name, Location currentLocation, VehicleType vehicleType) {
        this.id = requireText(id, "Driver ID");
        this.name = requireText(name, "Driver name");
        this.currentLocation = java.util.Objects.requireNonNull(currentLocation, "Location is required.");
        this.vehicleType = java.util.Objects.requireNonNull(vehicleType, "Vehicle type is required.");
    }

    private static String requireText(String value, String label) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(label + " is required.");
        }
        return value.trim();
    }

    public void setAvailable(boolean available) {
        if (available && activeRide) {
            throw new IllegalStateException("A driver assigned to a ride cannot be made available manually.");
        }
        this.available = available;
    }

    public void assignRide() {
        if (!available) {
            throw new IllegalStateException("Driver " + id + " is not available.");
        }
        available = false;
        activeRide = true;
        totalRidesAssigned++;
    }

    public void completeRide() {
        if (!activeRide) {
            throw new IllegalStateException("Driver " + id + " has no active ride.");
        }
        activeRide = false;
        available = true;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void updateLocation(Location currentLocation) {
        this.currentLocation = java.util.Objects.requireNonNull(currentLocation, "Location is required.");
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public boolean isAvailable() {
        return available;
    }

    public int getTotalRidesAssigned() {
        return totalRidesAssigned;
    }
}
