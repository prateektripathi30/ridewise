package com.ridewise.strategy;

import com.ridewise.model.Driver;
import com.ridewise.model.Rider;

import java.util.Comparator;
import java.util.List;

public final class LeastActiveDriverStrategy implements RideMatchingStrategy {
    @Override
    public Driver findDriver(Rider rider, List<Driver> drivers) {
        return drivers.stream()
                .min(Comparator.comparingInt(Driver::getTotalRidesAssigned)
                        .thenComparingDouble(
                                driver -> rider.getLocation().distanceTo(driver.getCurrentLocation())))
                .orElse(null);
    }
}
