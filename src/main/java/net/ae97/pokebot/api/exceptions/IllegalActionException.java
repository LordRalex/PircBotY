package net.ae97.pokebot.api.exceptions;

public class IllegalActionException extends RuntimeException {

    public IllegalActionException() {
        this("ATTEMPT MADE TO PERFORM ILLEGAL ACTION");
    }

    public IllegalActionException(String message) {
        super(message);
    }
}
