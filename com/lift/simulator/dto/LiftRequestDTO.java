package com.lift.simulator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LiftRequestDTO {
    private final int pickUpFloor;
    private final int dropOffFloor;
    private final int passengerCount;

    @JsonCreator
    public LiftRequestDTO(@JsonProperty("fromFloor") int pickUpFloor,
                          @JsonProperty("toFloor") int dropOffFloor,
                          @JsonProperty("noOfPassengers") int passengerCount) {
        this.pickUpFloor = pickUpFloor;
        this.dropOffFloor = dropOffFloor;
        this.passengerCount = passengerCount;
    }

    public int pickUpFloor() {
        return pickUpFloor;
    }

    public int dropOffFloor() {
        return dropOffFloor;
    }

    public int passengerCount() {
        return passengerCount;
    }

    @Override
    public String toString() {
        return "pickUpFloor:" + pickUpFloor + " dropOffFloor:" + dropOffFloor + " passengerCount:" + passengerCount;
    }
}