package lifts;

public class TemporaryLiftRequest {
    private int pickUpFloor;
    private int dropOffFloor;
    private int passengerCount;

    public int getPickUpFloor(){
        return this.pickUpFloor;
    }
    private void setPickUpFloor(int pickUpFloor){
        this.pickUpFloor=pickUpFloor;
    }

    public int getDropOffFloor(){
        return this.dropOffFloor;
    }
    private void setDropOffFloor(int dropOffFloor){
        this.dropOffFloor=dropOffFloor;
    }

    public int getPassengerCount(){
        return this.passengerCount;
    }
    private void setPassengerCount(int passengerCount){
        this.passengerCount=passengerCount;
    }

    public TemporaryLiftRequest(int pickUpFloor, int dropOffFloor, int passengerCount){
        setPickUpFloor(pickUpFloor);
        setDropOffFloor(dropOffFloor);
        setPassengerCount(passengerCount);
    }

    @Override
    public String toString(){
        return "pickUpFloor:" + getPickUpFloor() + " dropOffFloor:" +
                getDropOffFloor() + " passengerCount:" + getPassengerCount();
    }
}
