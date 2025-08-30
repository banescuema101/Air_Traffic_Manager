package org.example;
import java.io.PrintWriter;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class RunwayManagement {
    // The two hashMaps, associating: idRunway - The runway itself
    private Map<String, Runway<WideBodyAirplane>> wideRunwayMap;
    private Map<String, Runway<NarrowBodyAirplane>> narrowRunwayMap;

    // To store a list of flights, making it easier and more efficient to search
    // for a flight by its ID, for the flight_info command.
    private Map<String, Airplane> flightsMap;

    /**
    * Constructor of the class where I perform some operations on runways/flights.
    * Here I also initialize the hashMaps.
    */
    public RunwayManagement() {
        flightsMap = new HashMap<>();
        wideRunwayMap = new HashMap<>();
        narrowRunwayMap = new HashMap<>();
    }
    // Add a flight (an object of type Airplane) to the flights dictionary.
    public void addFlight(Airplane avion) {
        flightsMap.put(avion.getId(), avion);
    }
    // method for finding a flight by its id and returning it.
    public Airplane findFlightById(String id) {
        return flightsMap.get(id);
    }


    /**
    * Method that helps me remove an airplane from the queue of airplanes on a specific runway.
    * @param airplaneId the ID of the airplane I want to remove.
    * @param runwayId the ID of the runway from which I am removing an airplane.
    */
    public void eliminateFlight(String airplaneId, String runwayId) {
        Airplane airplane = flightsMap.get(airplaneId);
        if (airplane != null) {
            Runway<WideBodyAirplane> runwayWide = wideRunwayMap.get(runwayId);
            // I've found the runway, now I need to remove the airplane from its queue.
            if (runwayWide == null) {
                Runway<NarrowBodyAirplane> runwayNarrow = narrowRunwayMap.get(runwayId);
                runwayNarrow.deleteAirplane(airplaneId);
            } else {
                runwayWide.deleteAirplane(airplaneId);
            }
        }
        flightsMap.remove(airplaneId);
    }


    /**
    * Method to move a flight from one runway with a specific ID to another runway with a different ID.
    * Everything remains the same for that flight, except that it will belong to another runway. I ensure
    * that the type and usage of the airplane match the type and usage of the runway; otherwise, I display
    * the exception message thrown by the `addAirplane` method.
    * @param airplaneId the ID of the airplane I want to move.
    * @param runwayId the ID of the runway where the airplane with airplaneId is currently located.
    * @param newRunwayId the ID of the runway where I want to place the airplane with airplaneId.
    * @param pw //optional// a `PrintWriter` object where I display, if necessary, the exception
    *           generated when adding the airplane to the new runway.
    */
    public void moveFlight(String airplaneId, String runwayId, String newRunwayId, LocalTime timestamp, PrintWriter pw){
        Airplane airplane = flightsMap.get(airplaneId);
        eliminateFlight(airplaneId, runwayId);
        Runway<WideBodyAirplane> runwayWide = wideRunwayMap.get(newRunwayId);
        if (runwayWide != null) {
            try {
                // To ensure it is of type wide. A possible discrepancy between an airplane taking off
                // and a runway for landing, or vice versa, would result in catching the error and printing
                // the error message.
                if (airplane.getType().equals("wide")) {
                    runwayWide.addAirplane((WideBodyAirplane) airplane, timestamp);
                }
            } catch (IncorrectRunwayException e) {
                pw.println(e.getMessage());
            }
        } else {
            try {
                Runway<NarrowBodyAirplane> runwayNarrow = narrowRunwayMap.get(runwayId);
                if (airplane.getType().equals("narrow")) {
                    runwayNarrow.addAirplane((NarrowBodyAirplane) airplane, timestamp);
                }
            } catch (IncorrectRunwayException e) {
                pw.println(e.getMessage());
            }
        }
        addFlight(airplane);
    }


    /**
    * Method to delay a flight. The operator might have been informed that the airplane with airplaneId
    * has a delay. Therefore, the estimated time for that airplane to land/take off on/from
    * the corresponding runway will need to be updated.
    * @param airplaneId the ID of the airplane for which the desired time is being changed.
    * @param runwayId the ID of the runway where the airplane is located.
    * @param newTimestamp the new timestamp to set for the delayed airplane.
    */
    public void delayFlight(String airplaneId, String runwayId, LocalTime newTimestamp){
        Airplane airplane = flightsMap.get(airplaneId);
        if (airplane != null && (airplane.getStatus().equals(Status.WAITING_FOR_LANDING) || airplane.getStatus().equals(Status.WAITING_FOR_TAKEOFF))) {
            Runway<WideBodyAirplane> runwayWide = wideRunwayMap.get(runwayId);
            if (runwayWide != null) {
                runwayWide.modificaTimestampAvion(airplaneId, newTimestamp);
                flightsMap.get(airplaneId).setDesiredTime(newTimestamp);
                // but I also need to modify it in the flight-IDs.
            } else {
                Runway<NarrowBodyAirplane> narrowRunway = narrowRunwayMap.get(runwayId);
                narrowRunway.modificaTimestampAvion(airplaneId, newTimestamp);
            }
        }
    }

    /**
    * Method to add a specific runway to the wideRunwayMap dictionary as the value for the corresponding key -> the runway's ID.
    * @param runway1 a specific runway - containing only airplanes of type WideBodyAirplane.
    */
    public void addToWideList(Runway<WideBodyAirplane> runway1) {
        wideRunwayMap.put(runway1.getId(), runway1);
    }

    /**
    * Method in which I add to the narrowRunwayMap dictionary: at the position idRunway -> the runway.
    * @param runway2 a specific runway - containing only airplanes of type NarrowBodyAirplane
    */
    public void addToNarrowList(Runway<NarrowBodyAirplane> runway2) {
        narrowRunwayMap.put(runway2.getId(), runway2);
    }

    /**
    * Method to search for a runway with WideBody airplanes by its ID.
    * @param runwayId the ID of the runway I want to find.
    * @return the value associated with the key runwayId.
    */
    public Runway<WideBodyAirplane> findWideRunway(String runwayId) {
        return wideRunwayMap.get(runwayId);
    }

    /**
    * Method to search for a runway with NarrowBody airplanes by its ID.
    * @param runwayId the ID of the desired runway.
    * @return the value associated with the key runwayId.
    */
    public Runway<NarrowBodyAirplane> findNarrowRunway(String runwayId) {
        return narrowRunwayMap.get(runwayId);
    }

    /**
    * Method to display each runway with a specific ID (without knowing in this method exactly
    * what type of airplanes the runway contains - wide or narrow).
    * I use the helper method helperDisplayRunwayInfo.
    * @param runwayId the ID of the runway to display.
    * @param timestamp the timestamp at which the runway_info command is executed.
    * @param pw an instance of type PrintWriter (to which I assign a file descriptor in the Main class)
    *          where I will display the output.
    */
    public void displayRunwayInfo(String runwayId, LocalTime timestamp, PrintWriter pw) {
        helperDisplayRunwayInfo(findNarrowRunway(runwayId), timestamp, pw);
        helperDisplayRunwayInfo(findWideRunway(runwayId), timestamp, pw);
    }

    /**
    * Method that displays the runway ID, as well as all the airplanes in the runway's queue.
    * @param runway the desired runway. This will take the value returned by the findNarrowRunway function,
    *               and thus it will be a runway with NarrowBody airplanes if the function returns != null. Otherwise,
    *               I will call this method with the parameter runway taken from findWideRunway.
    * @param timestamp the timestamp at which the runway_info command is executed.
    * @param pw the PrintWriter.
    * @param <T> I assume that the runway in this function can take the type Runway<WideBodyAirplane> or
    *            Runway<NarrowBodyAirplane>.
    */
    public <T extends Airplane> void helperDisplayRunwayInfo(Runway<T> runway, LocalTime timestamp, PrintWriter pw) {
        if (runway != null) {
            // I also update the runway status here, because it would remain occupied
            // from maneuvers!! In the method for extracting an airplane from org.example.Runway,
            // I reset it to FREE by comparing it with the timestamp for the next extraction,
            // but here in the display, it needs to be reset again!
            if (runway.getRunwayStatus().equals(StatusRunway.OCCUPIED) && runway.occupiedRunwayTime().isBefore(timestamp)) {
                runway.setRunwayStatus(StatusRunway.FREE);
            }
            pw.println(runway.getId() + " - " + runway.getRunwayStatus());
            PriorityQueue<T> auxQueue = new PriorityQueue<>(runway.getAirplaneQueue());
            while (!auxQueue.isEmpty()) {
                T airplane = auxQueue.poll();
                if (!airplane.getStatus().equals(Status.DEPARTED) && !airplane.getStatus().equals(Status.LANDED)) {
                    pw.println(airplane);
                }
            }
        }
    }
}
