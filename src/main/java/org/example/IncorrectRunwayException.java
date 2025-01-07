package org.example;

/**
 * Clasa ce reprezinta exceptia cu numele specificat mai jos, al carei constructor face o exceptie
 * cu un anume mesaj dorit (va fi de forma timestamp | The chosen runway for
 * allocating the plane is incorrect)
 */
public class IncorrectRunwayException extends Exception {
    public IncorrectRunwayException(String mesaj) {
        super(mesaj);
    }
}
