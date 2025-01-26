## Introduction:
This is a Java project for Object-Oriented Programming (OOP), where I have applied various OOP principles such as inheritance, polymorphism, and encapsulation. The project also uses concepts like generics, collections, and exceptions. It models airplanes and runways and includes functionality for managing airplane statuses, runway operations, and flight management. The primary aim is to demonstrate the use of OOP techniques in a practical system for managing air traffic at an airport. The project also includes automated tests to ensure the functionality and correctness of the system.
# It can be run by executing the command `gradle test`

## Airplane Class:
This class models airplanes with the attributes specified in the prompt. The status is initialized based on the destination location. If the destination is Bucharest, the airplane’s status is set to WAITING_FOR_LANDING, otherwise, it is set to WAITING_FOR_TAKEOFF.

## Runway Class:
This class is modeled using generics, with <T extends Airplane>, to handle any type of airplane that extends the base Airplane class, such as WideBody and NarrowBody. The class represents a runway with a unique ID, its usage status (using the StatusRunway enum), and a field for the time a runway is occupied (LocalTime timeRunwayOccupied), which helps in managing the “dead time” of 5-10 minutes after a landing or takeoff.

A priority queue, PriorityQueue<T>, is used to manage the airplanes on the runway. The main advantage of using a priority queue is that it automatically maintains the order based on priorities, which in this case, are determined by the airplane type and its flight operation (landing or takeoff). Using PriorityQueue also ensures efficient insertion and removal operations with a time complexity of O(logn), compared to a list where manual sorting would be required.

Inside this class, there are methods for extracting an airplane (i.e., changing the status of the highest-priority airplane in the queue), adding an airplane to the queue, and removing an airplane based on its ID. When iterating through the priority queue to display its elements, an auxiliary queue is used to avoid altering the original queue.

## Runway Management Class:
This class is instantiated only once in the main class and provides methods for managing runways, such as removing a flight by ID (from the appropriate runway), displaying the flights on a specific runway, and handling operations like flight delays, cancellations, or transfers (as described in the bonus task).

# Runways are stored in two maps:

private Map<String, Runway<WideBodyAirplane>> wideRunwayMap
private Map<String, Runway<NarrowBodyAirplane>> narrowRunwayMap
Each map associates a runway ID with its respective runway object, allowing for quick lookup by ID. If the runway is not found in the wideRunwayMap, it is checked in the narrowRunwayMap. This enables efficient retrieval and adds/removes operations with constant time complexity O(1) for lookups.

Additionally, a HashMap<String, Airplane> flightsMap is used to keep track of flights, so they can be easily accessed and displayed. A small challenge is ensuring that when a flight is removed from a runway, it is also removed from the flightsMap to prevent displaying non-existent flights.

## Bonus Part:
For the bonus part, I implemented commands that allow modifying a flight’s status:

# timestamp change_flight delay id_flight id_runway timestamp: Changes the flight’s delay status.
# timestamp change_flight cancel id_flight id_runway: Cancels the flight.
# timestamp change_flight move id_flight id_runway id_new_runway: Moves the flight to a new runway, if compatible with its type (wide/narrow body) and operation (landing/takeoff).
#
# These operations allow for more dynamic control over the flights and runways, simulating real-time airport operations.
