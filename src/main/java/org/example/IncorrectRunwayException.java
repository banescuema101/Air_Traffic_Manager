package org.example;

/**
* The class that represents the exception with the specific name below, whose constructor creates an exception
* with a desired message (it will be in the format timestamp | The chosen runway for
* allocating the plane is incorrect)
*/
public class IncorrectRunwayException extends Exception {
    public IncorrectRunwayException(String message) {
        super(message);
    }
}
