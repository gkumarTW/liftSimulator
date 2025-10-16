package com.lift.simulator.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record LiftRequestDTO(int pickUpFloor, int dropOffFloor, int passengerCount) {
    @JsonCreator
    public LiftRequestDTO(
            @JsonProperty("fromFloor") int pickUpFloor,
            @JsonProperty("toFloor") int dropOffFloor,
            @JsonProperty("noOfPassengers") int passengerCount
    ) {
        this.pickUpFloor = pickUpFloor;
        this.dropOffFloor = dropOffFloor;
        this.passengerCount = passengerCount;
    }

    @Override
    public String toString() {
        return "pickUpFloor:" + pickUpFloor +
                " dropOffFloor:" + dropOffFloor +
                " passengerCount:" + passengerCount;
    }
}
