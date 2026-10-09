package com.ridewise.service;

import com.ridewise.model.Location;
import com.ridewise.model.Rider;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class RiderService {
    private final Map<String, Rider> riders = new LinkedHashMap<>();
    private long nextId = 1;

    public Rider registerRider(String name, Location location) {
        Rider rider = new Rider("R-" + nextId++, name, Objects.requireNonNull(location, "Location is required."));
        riders.put(rider.getId(), rider);
        return rider;
    }

    public Rider getRiderById(String riderId) {
        Rider rider = riders.get(riderId);
        if (rider == null) {
            throw new IllegalArgumentException("Rider not found: " + riderId);
        }
        return rider;
    }

    public List<Rider> listRiders() {
        return List.copyOf(riders.values());
    }
}
