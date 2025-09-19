package lifts;

import exception.RequestFloorsOutOfRangeException;
import utility.LiftRequest;

//An interface will hold public methods which will set a CONTRACT with classes that want to implement lifts.brands.NormalLift interface
public interface LiftI extends Runnable {

    void addRequest(LiftRequest newRequest) throws RequestFloorsOutOfRangeException;
    boolean canLiftFit(int passengerCount);
    void stopLift();
    void addPassengers(int passengerCount);

    int getLiftId();
    int getMinFloor();
    int getMaxFloor();
    int getCurrentCapacity();
    int getTotalCapacity();
    LiftStates getCurrState();
    int getCurrentFloor();
    String getLiftBrand();
}
