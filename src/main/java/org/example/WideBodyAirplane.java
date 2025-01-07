package org.example;

import java.time.LocalTime;

public class WideBodyAirplane extends Airplane {
    // constructorul avionului de tip WideBody, in care practic voi apela constructorul superclasei, adica
    // al clasei Airplane.
    public WideBodyAirplane(String model, String id, String locatiePlecare, String destinatie, LocalTime timpDorit, String urgent) {
        super("wide", model, id, locatiePlecare, destinatie, timpDorit, urgent);
    }
    /**
     * Metoda in care suprascriu metoda toString a superclasei, specificand in fata ca
     * prim argument, tipul avionului (Wide Body).
     * @return descrierea obiectului
     */
    public String toString() {
        return "Wide Body - " + super.toString();
    }
}
