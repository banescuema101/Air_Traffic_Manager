package org.example;

import java.util.Comparator;

public class RunwayComparatorLanding <T extends Airplane> implements Comparator<T> {
    /**
    * The method compares two airplanes to add them according to the priority calculated here
    * within the queue of a landing runway.
    * (1) If airplane1 has an emergency, as well as airplane2, only compare the desired landing times.
    * (2) If airplane1 has an emergency, but airplane2 does not, then airplane1 is given higher priority
    * to be extracted first from the queue.
    * (3) If airplane2 has an emergency, but airplane1 does not, then airplane2 is given higher priority.
    * (4) If neither has an emergency, then only compare the desired times.
    * @param airplane1 the first airplane to be compared in terms of landing time
    *                  and the emergency field it might have, with the second airplane
    * @param airplane2 the second airplane.
    * @return -1 if airplane1 has higher priority, 1 if airplane2 has higher priority,
    */
    @Override
    public int compare(T airplane1, T airplane2) {
        if (airplane1.getUrgentFlag() != null && airplane2.getUrgentFlag() != null) {
            return airplane1.getDesiredTime().compareTo(airplane2.getDesiredTime());
        } else if (airplane1.getUrgentFlag() != null) {
            return -1; // airplane1 has higher priority than airplane2
        } else if (airplane2.getUrgentFlag() != null) {
            return 1;
        } else if (airplane1.getUrgentFlag() == null && airplane2.getUrgentFlag() == null) {
            System.out.println(airplane1.getDesiredTime().isAfter(airplane2.getDesiredTime()));
            return airplane1.getDesiredTime().compareTo(airplane2.getDesiredTime());
        }
        return 0;
    }
}
