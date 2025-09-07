import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiftManager{
    private int maxFloors;
    private Map<Integer, Lift> liftsMap;

    LiftManager(int maxFloors, Lift... lifts){
        this.maxFloors=maxFloors;
        this.liftsMap=new ConcurrentHashMap<>();
        for(Lift lift: lifts){
            this.liftsMap.put(lift.liftId,lift);
        }
    }

    public int handleLiftRequest(LiftRequest request) throws InvalidFloorException, LiftFullException {
        // Floor validation
        if (request.fromFloor < 0 || request.fromFloor > maxFloors
                || request.toFloor < 0 || request.toFloor > maxFloors) {
            throw new InvalidFloorException("Invalid floor in request");
        }

        // Find suitable lift
        Lift nearestLift = findNearestLift(request);
        if (nearestLift == null) {
            throw new LiftFullException("No available lift could handle this request");
        }

        synchronized (nearestLift) {
            if (!canTakeRequest(nearestLift,request.passengerCount)) {
                throw new LiftFullException("Lift " + nearestLift.liftId + " is full");
            }
            nearestLift.addPassengers(request.passengerCount);
            nearestLift.addRequest(request);
        }

        return nearestLift.liftId;
    }

    private Lift findNearestLift(LiftRequest request) {
        Lift nearestLift = null;
        int minDistance = Integer.MAX_VALUE;

        for (Lift lift : liftsMap.values()) {
            // Skip if lift has no room
            if (!canTakeRequest(lift,request.passengerCount)) continue;

            int distance = Math.abs(lift.getCurrentFloor() - request.fromFloor);

            switch (lift.getCurrState()) {
                case idle:
                    // idle lifts are always candidates
                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestLift = lift;
                    }
                    break;

                case goingUp:
                    // request is upwards and lift is below or at fromFloor
                    if (request.fromFloor >= lift.getCurrentFloor()
                            && request.toFloor > request.fromFloor) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestLift = lift;
                        }
                    }
                    break;

                case goingDown:
                    // request is downwards and lift is above or at fromFloor
                    if (request.fromFloor <= lift.getCurrentFloor()
                            && request.toFloor < request.fromFloor) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestLift = lift;
                        }
                    }
                    break;
            }
        }

        return nearestLift;
    }

    public boolean canTakeRequest(Lift lift,int passengerCount){
        return (lift.getCurrentCapacity() + passengerCount) <= lift.getTotalCapacity();
    }
    public void startLifts(){
        for (Lift lift: liftsMap.values()){
            new Thread(lift).start();
        }
    }

    public void stopLifts(){
        for(Lift lift : liftsMap.values()){
            lift.stopLift();
        }
    }

}