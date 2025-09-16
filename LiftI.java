import java.util.Map;
import java.util.Queue;

//An interface will hold public methods
public interface LiftI extends Runnable {
    int getLiftId();
    int getMinFloor();
    int getMaxFloor();
    int getCurrentCapacity();
    int getTotalCapacity();
    LiftStates getCurrState();
    int getCurrentFloor();
    Queue<LiftRequest> getRequests();
    Map<Integer, Integer> getPickUpRequests();
    Map<Integer, Integer> getActiveDropOffRequests();
    String getLiftBrand();
    int getTotalCapacityLimit();

    void addRequest(LiftRequest newRequest) throws RequestFloorsOutOfRangeException;
    boolean canLiftFit(int passengerCount);
    void stopLift();
    void addPassengers(int passengerCount);

}
