package org.example;
import java.time.format.DateTimeFormatter;
import java.time.LocalTime;

/**
    * The statuses an airplane can have.
 */
enum Status {
    WAITING_FOR_TAKEOFF,
    DEPARTED,
    WAITING_FOR_LANDING,
    LANDED
}
public abstract class Airplane {
    private String type; // can be "narrow" or "wide", initialized in more specific classes.
    private String model;
    private String urgentFlag;
    private String id;
    private String departureLocation;
    private String destination;
    // The times are of type LocalTime, which I later formatted using DateTimeFormatter,
    // and parsed in the Main and Runway classes using the method
    // LocalTime.parse(String..., DateTimeFormatter ...)
    private LocalTime desiredTime;
    // The actual time will be assigned in the Runway class when extracting the airplane
    // from the queue of airplanes on a runway. Currently, it is not assigned in the Airplane constructor.
    private LocalTime actualTime;
    private Status status;

    /**
    * Constructor for an airplane
    * @param type the type of the airplane, either wide or narrow.
    * @param model the model of the airplane, assigned to the corresponding field.
    * @param id the ID associated with the airplane.
    * @param departureLocation the departure location, of type String. If it is "Bucharest" => it is an airplane
    *                          that wants to take off. The status is assigned accordingly.
    * @param destination the destination of the flight. If it is "Bucharest" => it is an airplane that wants to
    *                    land. The status is assigned accordingly.
    * @param desiredTime the time at which it is anticipated.
    * @param urgentFlag a field that can be null => the urgentFlag attribute of the airplane class remains null => it has
    *                   no urgency.
    *                   a field that can be the string "urgentFlag" => means the airplane has an urgency and will be treated
    *                   accordingly later.
    */
    public Airplane(String type, String model, String id, String departureLocation, String destination, LocalTime desiredTime, String urgentFlag) {
        if (urgentFlag != null) {
            this.urgentFlag = urgentFlag;
        }
        this.type = type;
        this.model = model;
        this.id = id;
        this.departureLocation = departureLocation;
        this.destination = destination;
        this.desiredTime = desiredTime;
        if (departureLocation.equals("Bucharest")) {
            status = Status.WAITING_FOR_TAKEOFF;
        } else if (destination.equals("Bucharest")) {
            status = Status.WAITING_FOR_LANDING;
        }
    }
    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDepartureLocation() {
        return departureLocation;
    }

    public void setDepartureLocation(String departureLocation) {
        this.departureLocation = departureLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalTime getDesiredTime() {
        return desiredTime;
    }

    public void setDesiredTime(LocalTime desiredTime) {
        this.desiredTime = desiredTime;
    }

    public LocalTime getActualTime() {
        return actualTime;
    }

    public void setActualTime(LocalTime actualTime) {
        this.actualTime = actualTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }
    public String getUrgentFlag() {
        return urgentFlag;
    }

    public void setUrgentFlag(String urgentFlag) {
        this.urgentFlag = urgentFlag;
    }

    /**
    * The toString method, which returns the representation of the airplane in two cases:
    * @return Case 1) if the actual time is null => the airplane lands/takes off at the desired time
    * initially provided in the input file from Main. In this case, all attributes of this class are displayed,
    * except for the actualTime attribute.
    *         Case 2) if the actual time is assigned => the return will also include its value.
    *
    * In both cases, the time is formatted to display with the ":" delimiter between hour:minute:seconds.
    * Additionally, if not formatted this way, the seconds part would not always display (e.g., if the time was
    * 13:45:00 => it would display as 13:45).
    */
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String desiredTimeFormattedWithSeconds = desiredTime.format(formatter);
        if (actualTime == null) {
            return this.model + " - " + this.id + " - " + this.departureLocation + " - " + this.destination + " - " + this.status + " - " + desiredTimeFormattedWithSeconds;
        }
        String actualTimeFormattedWithSeconds = actualTime.format(formatter);
        return this.model + " - " + this.id + " - " + this.departureLocation + " - " + this.destination + " - " + this.status + " - " + desiredTimeFormattedWithSeconds + " - " + actualTimeFormattedWithSeconds;
    }
}

