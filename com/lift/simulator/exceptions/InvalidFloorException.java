package com.lift.simulator.exceptions;

public class InvalidFloorException extends RuntimeException {
    public InvalidFloorException() {
        super("Invalid floor");
    }

    public InvalidFloorException(String message) {
        super(message);
    }
}
