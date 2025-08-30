package org.example;

import java.time.LocalTime;

public class WideBodyAirplane extends Airplane {
    // The constructor of the WideBody airplane, in which I will call the constructor of the superclass,
    // namely the Airplane class.
    public WideBodyAirplane(String model, String id, String departureLocation, String destination, LocalTime desiredTime, String urgentFlag) {
        super("wide", model, id, departureLocation, destination, desiredTime, urgentFlag);
    }

    /**
    * Method in which I override the toString method of the superclass, specifying
    * as the first argument the type of the airplane (Wide Body).
    * @return the description of the object
    */
    public String toString() {
        return "Wide Body - " + super.toString();
    }
}
