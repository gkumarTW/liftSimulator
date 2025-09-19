package utility;

public class LiftRequest {
    public final int fromFloor;
    public final int toFloor;
    public final int passengerCount;

    public LiftRequest(int fromFloor, int toFloor, int passengerCount) {
        this.passengerCount = passengerCount;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    @Override
    public String toString(){
        return "fromFloor: "+this.fromFloor+" toFloor: "+this.toFloor+" passengers: "+this.passengerCount;
    }
}