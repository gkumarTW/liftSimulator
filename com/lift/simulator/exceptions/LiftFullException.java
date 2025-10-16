package com.lift.simulator.exceptions;

public class LiftFullException extends Exception {
    public LiftFullException() {
        super("lifts.brands.NormalLift is full please try after some time");
    }
}
