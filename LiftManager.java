import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiftManager {
    private int maxFloors;
    private List<Lift> liftsList;

    LiftManager(int maxFloors, Lift... lifts) {
        this.maxFloors = maxFloors;
        this.liftsList = new LinkedList<>();
        this.liftsList.addAll(Arrays.asList(lifts));
    }

    //used to assign the lift for the request made
    public int handleLiftRequest(LiftRequest request) throws
            InvalidFloorException, LiftFullException {
        //Checking if the requested fromFloor and toFloor are in building's range
        if (request.fromFloor < 0 || request.fromFloor > maxFloors
                || request.toFloor < 0 || request.toFloor > maxFloors) {
            throw new InvalidFloorException("Invalid floor in request");
        }

        /* Find suitable lift (dependencies are request's fromFloor, toFloor, passengerCount
         * and lift's currentCapacity, totalCapacity, state )
         */
        Lift nearestLift = findNearestLift(request);

        //findNearestLift method will return null if no lift can fit the requested passengerCount
        if (nearestLift == null) {
            throw new LiftFullException();
        }

        nearestLift.addPassengers(request.passengerCount);
        nearestLift.addRequest(request);

        return nearestLift.liftId;
    }

    /* This method will return the nearest lift to the requested fromFloor based on distance(that is going
     * towards the request toFloor or the lift that is idle)
     */
    private Lift findNearestLift(LiftRequest request) {
        Lift nearestLift = null;
        int minDistance = Integer.MAX_VALUE;

        for (Lift lift : liftsList) {
            // check if lift can fit requested passengerCount
            if (lift.canLiftFit(request.passengerCount))
                continue;

            int distance = Math.abs(lift.getCurrentFloor() - request.fromFloor);

            switch (lift.getCurrState()) {
                case idle:

                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestLift = lift;
                    }
                    break;

                case goingUp:
                    if (request.fromFloor >= lift.getCurrentFloor()
                            && request.toFloor > request.fromFloor) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestLift = lift;
                        }
                    }
                    break;

                case goingDown:
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


    //method to start all the lifts inside the map
    public void startLifts() {
        for (Lift lift : liftsList) {
            new Thread(lift).start();
        }
    }

    //method to stop all the lifts inside the map
    public void stopLifts() {
        for (Lift lift : liftsList) {
            lift.stopLift();
        }
    }

}