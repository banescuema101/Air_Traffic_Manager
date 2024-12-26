package org.example;

import java.util.Comparator;

public class RunwayComparatorLanding <T extends Airplane> implements Comparator<T> {
    @Override

    public int compare(T airplane1, T airplane2) {
        if (airplane1.getUrgent() != null && airplane2.getUrgent() != null) {
            return airplane1.getTimpDorit().compareTo(airplane2.getTimpDorit());
        } else if (airplane1.getUrgent() != null) {
            return -1; // airplane1 va a vea prioritate mai mare ca a lui airplane2.
        } else if (airplane2.getUrgent() != null) {
            return 1;
        } else if (airplane1.getUrgent() == null && airplane2.getUrgent() == null) {
            System.out.println(airplane1.getTimpDorit().isAfter(airplane2.getTimpDorit()));
            if (airplane1.getTimpDorit().isAfter(airplane2.getTimpDorit())) {
                return 1;
            } else if (airplane1.getTimpDorit().isBefore(airplane2.getTimpDorit())) {
                return -1;
            } else {
                return 0;
            }
        }
        return 0;
    }
}
