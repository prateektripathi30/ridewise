package com.ridewise;

import com.ridewise.model.Driver;
import com.ridewise.model.FareReceipt;
import com.ridewise.model.Location;
import com.ridewise.model.Ride;
import com.ridewise.model.Rider;
import com.ridewise.model.VehicleType;
import com.ridewise.service.DriverService;
import com.ridewise.service.RideService;
import com.ridewise.service.RiderService;
import com.ridewise.strategy.DefaultFareStrategy;
import com.ridewise.strategy.NearestDriverStrategy;

import java.util.Locale;
import java.util.Scanner;

public final class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final RiderService riderService = new RiderService();
    private final DriverService driverService = new DriverService();
    private final RideService rideService = new RideService(
            riderService,
            driverService,
            new NearestDriverStrategy(),
            new DefaultFareStrategy());

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        boolean running = true;
        while (running) {
            printMenu();
            String choice = readLine("Choose an option: ");
            try {
                switch (Integer.parseInt(choice)) {
                    case 1 -> addRider();
                    case 2 -> addDriver();
                    case 3 -> showAvailableDrivers();
                    case 4 -> requestRide();
                    case 5 -> completeRide();
                    case 6 -> showRides();
                    case 7 -> cancelRide();
                    case 8 -> {
                        running = false;
                        System.out.println("Goodbye.");
                    }
                    default -> System.out.println("Choose a number from 1 to 8.");
                }
            } catch (NumberFormatException exception) {
                System.out.println("Enter a valid number.");
            } catch (IllegalArgumentException | IllegalStateException exception) {
                System.out.println("Unable to complete action: " + exception.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("""

                === RideWise ===
                1. Add Rider
                2. Add Driver
                3. View Available Drivers
                4. Request Ride
                5. Complete Ride
                6. View Rides
                7. Cancel Ride
                8. Exit""");
    }

    private void addRider() {
        String name = readLine("Rider name: ");
        Location location = readLocation();
        Rider rider = riderService.registerRider(name, location);
        System.out.println("Registered rider " + rider.getId() + " (" + rider.getName() + ").");
    }

    private void addDriver() {
        String name = readLine("Driver name: ");
        Location location = readLocation();
        VehicleType vehicleType = readVehicleType();
        Driver driver = driverService.registerDriver(name, location, vehicleType);
        System.out.println("Registered driver " + driver.getId() + " (" + driver.getName()
                + ", " + driver.getVehicleType() + ").");
    }

    private void showAvailableDrivers() {
        var drivers = driverService.listAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers are currently available.");
            return;
        }
        drivers.forEach(driver -> System.out.printf(
                Locale.ROOT,
                "%s | %s | %s | location %.5f, %.5f | assigned rides %d%n",
                driver.getId(),
                driver.getName(),
                driver.getVehicleType(),
                driver.getCurrentLocation().latitude(),
                driver.getCurrentLocation().longitude(),
                driver.getTotalRidesAssigned()));
    }

    private void requestRide() {
        String riderId = readLine("Rider ID: ");
        double distance = readDouble("Ride distance in kilometres: ");
        VehicleType vehicleType = readVehicleType();
        Ride ride = rideService.requestRide(riderId, distance, vehicleType);
        if (ride.getDriver() == null) {
            System.out.println("Ride " + ride.getId() + " is REQUESTED; no matching driver is currently available.");
        } else {
            System.out.println("Ride " + ride.getId() + " assigned to " + ride.getDriver().getName()
                    + " (" + ride.getDriver().getId() + ").");
        }
    }

    private void completeRide() {
        String rideId = readLine("Ride ID: ");
        FareReceipt receipt = rideService.completeRide(rideId);
        System.out.printf(Locale.ROOT, "Ride completed. Fare: %.2f (receipt for %s)%n",
                receipt.amount(), receipt.rideId());
    }

    private void showRides() {
        var rides = rideService.listRides();
        if (rides.isEmpty()) {
            System.out.println("No rides have been requested.");
            return;
        }
        for (Ride ride : rides) {
            String driver = ride.getDriver() == null ? "unassigned"
                    : ride.getDriver().getName() + " (" + ride.getDriver().getId() + ")";
            System.out.printf(Locale.ROOT, "%s | rider %s | driver %s | %s | %s | %.2f km%n",
                    ride.getId(),
                    ride.getRider().getId(),
                    driver,
                    ride.getVehicleType(),
                    ride.getStatus(),
                    ride.getDistance());
        }
    }

    private void cancelRide() {
        String rideId = readLine("Ride ID: ");
        rideService.cancelRide(rideId);
        System.out.println("Ride " + rideId + " cancelled.");
    }

    private Location readLocation() {
        double latitude = readDouble("Latitude (-90 to 90): ");
        double longitude = readDouble("Longitude (-180 to 180): ");
        return new Location(latitude, longitude);
    }

    private VehicleType readVehicleType() {
        String value = readLine("Vehicle type (BIKE, AUTO, CAR): ").trim().toUpperCase(Locale.ROOT);
        return VehicleType.valueOf(value);
    }

    private double readDouble(String prompt) {
        String value = readLine(prompt);
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Enter a valid number: " + value);
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
