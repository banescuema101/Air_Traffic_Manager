package org.example;

import java.time.LocalTime;

public class NarrowBodyAirplane extends Airplane {
    // constructorul avionului de tip NarrowBody, in care practic voi apela constructorul superclasei, adica
    // al clasei Airplane.
    public NarrowBodyAirplane(String model, String id, String locatiePlecare, String destinatie, LocalTime timpDorit, String urgent) {
        super("narrow", model, id, locatiePlecare, destinatie, timpDorit, urgent);
    }

    /**
     * Metoda in care suprascriu metoda toString a superclasei, specificand in fata ca
     * prim argument, tipul avionului ( Narrow Body).
     * @return descrierea obiectului.
     */
    public String toString() {
        return "Narrow Body - " + super.toString();
    }
}
