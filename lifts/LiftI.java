package lifts;

import exception.RequestFloorsOutOfRangeException;
import lifts.LiftRequest;

//An interface will hold public methods which will set a CONTRACT with classes that want to implement lifts.brands.NormalLift interface
public interface LiftI extends Runnable {

    void addRequest(LiftRequest newRequest) throws RequestFloorsOutOfRangeException;
    boolean canLiftFit(int passengerCount);
    void stopLift();
    void addPassengers(int passengerCount);

    int getBuildingId();
    int getLiftId();
    int getMinFloor();
    int getMaxFloor();
    int getCurrentFloor();
    int getCurrentCapacity();
    int getTotalCapacity();
    LiftStates getCurrState();
    String getLiftBrand();
    int getBrandId();
    long getFloorTravelTimeMs();
    long getBoardingTimeMs();
}
