package org.example;

/**
* This class represents the exception with the specified name below, whose constructor creates an exception
* with a desired message (it will be in the format timestamp | The chosen runway for
* maneuver is currently occupied)
*/
public class UnavailableRunwayException extends Exception {
    public UnavailableRunwayException(String message) {
        super(message);
    }
}
