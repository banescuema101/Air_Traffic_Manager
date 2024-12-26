package org.example;

import java.time.LocalTime;

public class WideBodyAirplane extends Airplane {
    public WideBodyAirplane(String model, String id, String locatiePlecare, String destinatie, LocalTime timpDorit, String urgent) {
        super(model, id, locatiePlecare, destinatie, timpDorit, urgent);
    }

    public String toString() {
        return "Wide Body - " + super.toString();
    }
}
