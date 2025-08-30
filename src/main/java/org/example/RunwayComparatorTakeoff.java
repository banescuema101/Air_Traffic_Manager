package org.example;

import java.util.Comparator;

public class RunwayComparatorTakeoff<T extends Airplane> implements Comparator<T> {
    /**
    * Override of the compare method, where if the desired takeoff time of the first
    * airplane, airplane1, is earlier than the desired takeoff time of airplane2, then
    * higher priority will be assigned to the first one, meaning airplane1 will be extracted
    * first from the queue later.
    * @param airplane1 The first airplane to compare
    * @param airplane2 The second airplane
    * @return -1, if airplane1 has higher priority than airplane2
    *          1, otherwise
    *          0, if they have equal takeoff times.
    */
    @Override
    public int compare(T airplane1, T airplane2) {
        if (airplane1.getDesiredTime().equals(airplane2.getDesiredTime())) {
            return 0;
        } else if (airplane1.getDesiredTime().isAfter(airplane2.getDesiredTime())) {
            return 1;
        } else {
            return -1;
        }
    }
}
