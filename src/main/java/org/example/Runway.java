package org.example;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.PriorityQueue;

// Runway status
enum StatusRunway{
    FREE,
    OCCUPIED
}

/**
* Class representing a runway that contains a queue of airplanes, where I am allowed
* to work only with the parameter T.
* @param <T> A data type that extends the {@link Airplane} class (in our case, it can be:
*           {@link WideBodyAirplane} or {@link NarrowBodyAirplane})
*/
public class Runway<T extends Airplane> {
    private String id;
    private String usage;
    private PriorityQueue<T> airplaneQueue;
    private StatusRunway runwayStatus;
    // time until which the runway is occupied (if it is occupied)
    private LocalTime runwayOccupiedTime;

    /**
    * Airplane runway constructor
    * @param id the ID associated with the runway
    * @param usage what it is used for: landing / takeoff.
    * @param acceptedAirplaneType the type of airplane it accepts.
    * @param comparator A comparator of type Comparator<T>, for which I later create two classes:
    *                   {@link RunwayComparatorLanding} with the comparison criteria based on which
    *                   I will maintain the elements in the airplane queue for a landing runway, and
    *                   {@link RunwayComparatorTakeoff} with criteria for takeoff runways.
    * Initially, I assign the runway status as FREE.
    */
    public Runway(String id, String usage, String acceptedAirplaneType, Comparator<T> comparator) {
        this.id = id;
        this.usage = usage;
        this.airplaneQueue = new PriorityQueue<>(comparator);
        this.runwayStatus = StatusRunway.FREE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsage() {
        return usage;
    }

    public void setUsage(String usage) {
        this.usage = usage;
    }

    public PriorityQueue<T> getAirplaneQueue() {
        return airplaneQueue;
    }

    public void setAirplaneQueue(PriorityQueue<T> airplaneQueue) {
        this.airplaneQueue = airplaneQueue;
    }

    public StatusRunway getRunwayStatus() {
        return runwayStatus;
    }

    public void setRunwayStatus(StatusRunway runwayStatus) {
        this.runwayStatus = runwayStatus;
    }

    public LocalTime occupiedRunwayTime() {
        return runwayOccupiedTime;
    }

    public void setRunwayOccupiedTime(LocalTime runwayOccupiedTime) {
        this.runwayOccupiedTime = runwayOccupiedTime;
    }

    /**
    * Method that adds an airplane to the runway's airplane queue, checking the conditions
    * for the airplane's compatibility with the runway's usage type. If the conditions are not met => an exception is thrown.
    * @param airplane The airplane of type Airplane that I want to add to the runway's queue.
    * @param timestamp The timestamp when the command was executed, to display it in case
    *                  an exception is thrown (e.g., if the airplane wants to land but the runway is for takeoff).
    * @throws IncorrectRunwayException Exception thrown if the runway's usage (takeoff/landing) does not match
    *                                  the practical usage of the airplane (whether it is landing/taking off).
    */
    public void addAirplane(T airplane, LocalTime timestamp) throws IncorrectRunwayException {
        if ("Bucharest".equals(airplane.getDestination()) && "takeoff".equals(this.getUsage())
            || !("Bucharest".equals(airplane.getDestination())) && "landing".equals(this.getUsage())) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String timestampFormatat = timestamp.format(formatter);
            throw new IncorrectRunwayException(timestampFormatat + " | The chosen runway for allocating the plane is incorrect");
        } else {
            airplaneQueue.offer(airplane);
        }
    }


    /**
    * Method that updates the timestamp of an airplane with a specific ID from the airplane queue
    * on the runway.
    * @param airplaneId the ID of the airplane whose timestamp is being updated.
    * @param newTimestamp the new desired timestamp.
    */
    public void modificaTimestampAvion(String airplaneId, LocalTime newTimestamp){
        PriorityQueue<T> auxQueue = new PriorityQueue<>(airplaneQueue);
        while(!auxQueue.isEmpty()) {
            // the airplane I want to modify
            T airplane = auxQueue.poll();
            if (airplane.getId().equals(airplaneId)) {
                // I remove it from the runway's airplane queue
                airplaneQueue.remove(airplane);
                // I update its desired time
                airplane.setDesiredTime(newTimestamp);
                // I reinsert it into the runway's airplane queue
                airplaneQueue.offer(airplane);
            }
        }
    }

    /**
    * The method is responsible for appropriately modifying the status of the airplane that is about to be maneuvered
    * based on the following criteria:
    * 1) If the runway status is currently OCCUPIED, but the time until which it is occupied has expired,
    * meaning the "runwayOccupiedTime" is before the timestamp at which the permission_for_maneuver command was executed,
    * I will assign the status FREE to the runway.
    * 2) If the runway is FREE:
    *      I extract from the airplane queue (using an auxiliary queue, from which I keep removing elements) the first airplane
    *      that HAS THE STATUS WAITING_FOR_LANDING OR WAITING_FOR_TAKEOFF. If it exists, then I modify
    *      its status (LANDED/DEPARTED) and also set the actualTime to the time at which the maneuver was executed.
    *      Additionally, I update the time until which the runway will be occupied (5/10 minutes depending on
    *      the type of runway).
    * 3)   If the runway status is OCCUPIED, I throw the exception {@link UnavailableRunwayException}, which will contain as
    * a message the timestamp at which the maneuver attempt was made and the error message that the runway is
    * occupied.
    * @param timestamp The time at which the permission_for_maneuver command is executed.
    * @throws UnavailableRunwayException if the runway is occupied at that moment.
    */
    public void airplaneExtraction(LocalTime timestamp) throws UnavailableRunwayException {
        // I don't want to delete it, just "extract" it (change the airplane's status, the runway's status,
        // and the time for which it will or will not be occupied for other maneuvers).
        if (this.getRunwayStatus().equals(StatusRunway.OCCUPIED) && this.occupiedRunwayTime().isBefore(timestamp)) {
            this.setRunwayStatus(StatusRunway.FREE);
        }
        if (this.getRunwayStatus().equals(StatusRunway.FREE)) {
            // create an auxiliary queue from which I will extract elements in the priority order
            // corresponding to the "airplaneQueue".
            PriorityQueue<T> auxQueue = new PriorityQueue<>(airplaneQueue);
            T extractedAirplane = null;
            boolean found = false;
            while(!auxQueue.isEmpty()) {
                T airplane = auxQueue.poll();
                System.out.println("airplane-extraction-test:" + airplane);
                if (!airplane.getStatus().equals(Status.LANDED) && !airplane.getStatus().equals(Status.DEPARTED)) {
                    extractedAirplane = airplane;
                    found = true;
                    break;
                }
            }
            if (found) {
                if (extractedAirplane.getStatus().equals(Status.WAITING_FOR_LANDING)) {
                    extractedAirplane.setStatus(Status.LANDED);
                    extractedAirplane.setActualTime(timestamp);
                } else if (extractedAirplane.getStatus().equals(Status.WAITING_FOR_TAKEOFF)) {
                    extractedAirplane.setStatus(Status.DEPARTED);
                    extractedAirplane.setActualTime(timestamp);
                }

                this.setRunwayStatus(StatusRunway.OCCUPIED);
                // updating the status and the time until the runway will be occupied
                if (this.getUsage().equals("landing")) {
                    LocalTime timestampDelay1 = timestamp.plusMinutes(10);
                    this.setRunwayOccupiedTime(timestampDelay1);
                    System.out.println("The time until which the landing runway is occupied is: " + timestampDelay1);
                } else if (this.getUsage().equals("takeoff")) {
                    LocalTime timestampDelay2 = timestamp.plusMinutes(5);
                    this.setRunwayOccupiedTime(timestampDelay2);
                    System.out.println("The time until which the takeoff runway is occupied is: " + timestampDelay2);
                }
            }
        } else {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String formattedTimestampWithSeconds =  timestamp.format(formatter);
            throw new UnavailableRunwayException(formattedTimestampWithSeconds  + " | " + "The chosen runway for maneuver is currently occupied");
        }
    }

    /**
    * FOR BONUS:
    * Method that deletes an airplane with the given ID.
    * I create an auxiliary queue with the elements from the {@link Runway#airplaneQueue} queue and extract them one by one
    * until I reach the airplane with the desired ID. Then I will remove it from the airplane queue.
    * @param airplaneId the ID of the airplane to delete.
    */
    public void deleteAirplane(String airplaneId) {
        PriorityQueue<T> auxQueue = new PriorityQueue<>(airplaneQueue);
        while (!auxQueue.isEmpty()) {
            T airplane = auxQueue.poll();
            if (airplane.getId().equals(airplaneId)) {
                this.airplaneQueue.remove(airplane);
            }
        }
    }

    /**
    * toString method where I display:
    * @return the runway ID (on a separate line), and then on each line, the airplanes in the queue,
    * the queue being already sorted, as it is a priority queue, with the comparator specified at creation.
    */
    public String toString() {
        StringBuilder string = new StringBuilder();
        string.append(this.id);
        string.append("\n");
        PriorityQueue<T> auxQueue = new PriorityQueue<>(airplaneQueue);
        while (!auxQueue.isEmpty()) {
            string.append(auxQueue.poll().toString());
            string.append("\n");
        }
        return string.toString();
    }
}

