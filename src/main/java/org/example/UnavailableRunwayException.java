package org.example;

/**
 * Clasa ce reprezinta exceptia cu numele specificat mai jos, al carei constructor face o exceptie
 * cu un anume mesaj dorit (va fi de forma timestamp | The chosen runway for
 * maneuver is currently occupied)
 */
public class UnavailableRunwayException extends Exception {
    public UnavailableRunwayException(String mesaj) {
        super(mesaj);
    }
}
