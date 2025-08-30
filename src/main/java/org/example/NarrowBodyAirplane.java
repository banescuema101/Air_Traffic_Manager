package org.example;

import java.time.LocalTime;

public class NarrowBodyAirplane extends Airplane {
    // Constructor for the NarrowBody airplane, where I essentially call the superclass constructor,
    // which is the constructor of the Airplane class.
    public NarrowBodyAirplane(String model, String id, String departureLocation, String destination, LocalTime desiredTime, String urgentFlag) {
        super("narrow", model, id, departureLocation, destination, desiredTime, urgentFlag);
    }

    /**
    * Method in which I override the toString method of the superclass, specifying
    * as the first argument the type of the airplane (Narrow Body).
    * @return the description of the object.
    */
    public String toString() {
        return "Narrow Body - " + super.toString();
    }
}
