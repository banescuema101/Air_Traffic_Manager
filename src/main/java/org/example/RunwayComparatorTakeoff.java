package org.example;

import java.util.Comparator;

public class RunwayComparatorTakeoff<T extends Airplane> implements Comparator<T> {
    /**
     * Suprascrierea metodei compare, in care daca timpul de decolare dorit de catre primul
     * avion, airplane1 este inaintea timpului dorit de decolare de catre airplane2, atunci
     * ii voi aferi prioritatea de mare primului, adica airplane1 va fi extras primul din coada
     * ulterior.
     * @param airplane1 Primul avion, de comparat
     * @param airplane2 Cel de-al doilea avion
     * @return -1, daca airplane1 are prioritate mai mare fata de airplane2
     *          1, in caz contrar
     *          0, daca au timpi de decolare egali.
     */
    @Override
    public int compare(T airplane1, T airplane2) {
        if (airplane1.getTimpDorit().equals(airplane2.getTimpDorit())) {
            return 0;
        } else if (airplane1.getTimpDorit().isAfter(airplane2.getTimpDorit())) {
            return 1;
        } else {
            return -1;
        }
    }
}
