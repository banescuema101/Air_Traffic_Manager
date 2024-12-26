package org.example;

import java.time.LocalTime;

public class NarrowBodyAirplane extends Airplane {
    public NarrowBodyAirplane(String model, String id, String locatiePlecare, String destinatie, LocalTime timpDorit, String urgent) {
        super(model, id, locatiePlecare, destinatie, timpDorit, urgent);
    }

    public String toString() {
        return "Narrow Body - " + super.toString();
    }
}
