package com.vuelosapp.flightreservation.exception;

public class NotEnoughSeatsException extends RuntimeException {
    public NotEnoughSeatsException(String message) {
        super(message);
    }
}
