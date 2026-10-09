package com.ridewise.strategy;

import com.ridewise.model.Ride;

public interface FareStrategy {
    double calculateFare(Ride ride);
}
