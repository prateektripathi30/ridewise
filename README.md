# RideWise

RideWise is a small, in-memory Java console application for registering riders and drivers, matching rides, calculating fares, and tracking ride state. It is intentionally an MVP: it has no database, web server, authentication, or external dependencies.

## Open and run

Open `C:\Users\prate\IdeaProjects\ridewise` in IntelliJ IDEA as a Maven project. The project targets Java 17 or newer.

From PowerShell, run with Maven:

```powershell
mvn package
java -cp target\classes com.ridewise.Main
```

Or compile and run directly with the JDK:

```powershell
$sources = Get-ChildItem -Recurse src\main\java\*.java | ForEach-Object FullName
javac -d out $sources
java -cp out com.ridewise.Main
```

## Project layout

```text
src/main/java/com/ridewise/
  Main.java
  model/       Rider, Driver, Ride, FareReceipt, Location, and enums
  service/     RiderService, DriverService, RideService
  strategy/    Matching and fare strategy interfaces and implementations
```

## How it works

- `RiderService` and `DriverService` own registration and lookup. Generated IDs are stable for the lifetime of the process.
- `RideService` coordinates collaborators: it validates a request, filters available drivers to the requested vehicle type, delegates selection, and assigns a driver if one is found. A request remains `REQUESTED` when there is no suitable driver.
- A ride moves from `REQUESTED` to `ASSIGNED` to `COMPLETED`, or to `CANCELLED` from either non-terminal state. Completing a ride creates its `FareReceipt` and makes the driver available again. A driver can handle at most one ride at a time.
- `NearestDriverStrategy` uses great-circle distance between latitude/longitude locations. `LeastActiveDriverStrategy` chooses the eligible driver with the fewest lifetime assignments, using distance as a tie-breaker.
- `DefaultFareStrategy` charges the vehicle's per-kilometre rate. `PeakHourFareStrategy` decorates another fare strategy and applies a configurable multiplier during the local morning/evening peak windows.
- The default composition in `Main` uses nearest-driver matching and default fares. `RideService` accepts the two strategy interfaces in its constructor, so either policy can be replaced without changing ride orchestration.

The console offers rider/driver registration, available-driver listing, ride requests, ride completion, ride cancellation, and ride history. Invalid menu choices, numeric input, IDs, and state transitions are reported without terminating the application.

## Design notes

- **SRP:** entities hold domain state; services coordinate use cases; strategies contain replaceable algorithms; `Main` handles console I/O.
- **OCP / LSP:** new matching or fare implementations can implement the small strategy interfaces and be substituted.
- **ISP / DIP:** `RideService` depends on `RideMatchingStrategy` and `FareStrategy`, not implementation classes.
- **DRY / KISS / YAGNI:** allocation, validation, and lifecycle rules are centralized; the MVP avoids unrelated infrastructure.
- **Law of Demeter:** services communicate with direct collaborators instead of relying on long object-navigation chains.

All data is held in memory and is lost when the application exits. Locations are latitude/longitude in decimal degrees; ride distance is entered in kilometres. Fare rates and receipts are generic fare units because no currency was specified.
