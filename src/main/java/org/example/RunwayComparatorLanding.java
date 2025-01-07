package org.example;

import java.util.Comparator;

public class RunwayComparatorLanding <T extends Airplane> implements Comparator<T> {
    /**
     * Metoda comparare a doua avioane pentru a le adauga comform prioritatii calculate aici
     * in cadrul cozii unei piste de aterizare.
     * (1) Daca airplane 1 are o urgenta, cat si airplane2, compar doar timpii doriti de aterizare.
     * (2) Daca airplane 1 are o urgenta, dar airplane2 nu, atunci lui airplane1 ii acord prioritate mai mare
     * pentru a il extrage primul din coada.
     * (3) Daca airplane 2 are urgenta, dar airplane1 nu, atunci lui airplane2 ii acord prioritate mai mare
     * (4) Daca niciunul nu are nicio urgenta, atunci doar compar timpii doriti.
     * @param airplane1 primul avion care va fi comparat d.p.d.v al timpului de aterizare
     *                  si al campului urgent pe care l-ar putea avea, cu cel de-al doilea avion
     * @param airplane2 cel de-al doilea avion.
     * @return
     */
    @Override
    public int compare(T airplane1, T airplane2) {
        if (airplane1.getUrgent() != null && airplane2.getUrgent() != null) {
            return airplane1.getTimpDorit().compareTo(airplane2.getTimpDorit());
        } else if (airplane1.getUrgent() != null) {
            return -1; // airplane1 va avea prioritate mai mare ca a lui airplane2.
        } else if (airplane2.getUrgent() != null) {
            return 1;
        } else if (airplane1.getUrgent() == null && airplane2.getUrgent() == null) {
            System.out.println(airplane1.getTimpDorit().isAfter(airplane2.getTimpDorit()));
            return airplane1.getTimpDorit().compareTo(airplane2.getTimpDorit());
        }
        return 0;
    }
}
