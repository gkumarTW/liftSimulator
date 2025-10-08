package main.resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class LiftRequestJSONUtil {
    private int pickUpFloor;
    private int dropOffFloor;
    private int passengerCount;

    @JsonCreator
    public LiftRequestJSONUtil(
            @JsonProperty("fromFloor") int pickUpFloor,
            @JsonProperty("toFloor") int dropOffFloor,
            @JsonProperty("noOfPassengers") int passengerCount
    ) {
        this.pickUpFloor = pickUpFloor;
        this.dropOffFloor = dropOffFloor;
        this.passengerCount = passengerCount;
    }

    public int getPickUpFloor() { return pickUpFloor; }
    public int getDropOffFloor() { return dropOffFloor; }
    public int getPassengerCount() { return passengerCount; }

    @Override
    public String toString() {
        return "pickUpFloor:" + pickUpFloor +
                " dropOffFloor:" + dropOffFloor +
                " passengerCount:" + passengerCount;
    }

}
