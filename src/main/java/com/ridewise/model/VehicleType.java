package com.ridewise.model;

public enum VehicleType {
    BIKE(2.0),
    AUTO(3.0),
    CAR(5.0);

    private final double farePerKilometre;

    VehicleType(double farePerKilometre) {
        this.farePerKilometre = farePerKilometre;
    }

    public double getFarePerKilometre() {
        return farePerKilometre;
    }
}
