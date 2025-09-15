import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LiftManager {
    private int totalLifts;
    private int minFloor = 0;//In the building
    private int maxFloor;//In the building
    private int serviceFloors;
    private int maxLiftsCapacity;
    private List<Lift> liftsList = new LinkedList<>();

    //get methods for private variables
    public int getTotalLifts() {
        return this.totalLifts;
    }

    public int getMinFloor() {
        return this.minFloor;
    }

    public int getMaxFloor() {
        return this.maxFloor;
    }

    public int getServiceFloors() {
        return this.serviceFloors;
    }

    public int getMaxCapacityOfLifts() {
        return this.maxLiftsCapacity;
    }

    LiftManager() {
    }

    LiftManager(int maxFloor, Lift... lifts) {
        this.maxFloor = maxFloor;
        this.liftsList = new LinkedList<>();
        this.liftsList.addAll(Arrays.asList(lifts));
    }

    LiftManager(int maxFloor, List<Lift> lifts) {
        //A LinkedList should hold all the lifts
        this.liftsList = new LinkedList<>(lifts);
    }

    public void inputBuildingConfiguration(Scanner sc) {
        System.out.println("Please configure the building...");

        while (true) {
            System.out.println("Enter total floors:");
            int maxFloorInput = sc.nextInt();
            if (maxFloorInput < 0) {
                System.out.println("Invalid input");
                continue;
            }
            this.maxFloor = maxFloorInput;
            break;
        }
        while (true) {
            System.out.println("Number of lifts: ");
            int totalLiftsCount = sc.nextInt();
            if (totalLiftsCount < 0) {
                System.out.println("Invalid input");
                continue;
            }
            this.totalLifts = totalLiftsCount;
            break;
        }
    }

    public void inputLifts(Scanner sc) {
        int maxFloorLiftCanService = 0;
        int maxCapacityOfLifts = 0;
        while (liftsList.size() != totalLifts) {
            int currentLiftId = liftsList.size() + 1;
            System.out.println("Configure lift " + currentLiftId);
            int currentLiftMaxFloor, currentLiftMinFloor, currentLiftMaxCapacity;
            while (true) {
                try {
                    System.out.println("Max floors for this lift:");
                    int input = sc.nextInt();
                    if (input > maxFloor || input <= 0)
                        throw new InvalidInputException();
                    currentLiftMaxFloor = input;
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                try {
                    System.out.println("Max passengers for this lift:");
                    int input = sc.nextInt();
                    if (input <= 0)
                        throw new InvalidInputException();
                    currentLiftMaxCapacity = input;
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            currentLiftMinFloor = 0;

            Lift currentLift = new Lift(liftsList.size() + 1,
                    currentLiftMinFloor, currentLiftMaxFloor, currentLiftMaxCapacity);

            liftsList.add(currentLift);

            System.out.println("Created lift with id: " + currentLift.liftId);

            if (currentLiftMaxFloor > maxFloorLiftCanService)
                maxFloorLiftCanService = currentLiftMaxFloor;

            if (currentLiftMaxCapacity > maxCapacityOfLifts)
                maxCapacityOfLifts = currentLiftMaxCapacity;
        }

        this.serviceFloors = maxFloorLiftCanService;
        this.maxLiftsCapacity = maxCapacityOfLifts;
    }


    //used to assign the lift for the request made
    public int handleLiftRequest(LiftRequest request) throws
            InvalidFloorException, LiftFullException, RequestFloorsOutOfRangeException {
        //Checking if the requested fromFloor and toFloor are in building's range
        if (request.fromFloor < 0 || request.fromFloor > this.maxFloor
                || request.toFloor < 0 || request.toFloor > this.maxFloor) {
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
            //check if request from and to floor is within this lift's limit
            if (request.fromFloor < lift.minFloor || request.toFloor < lift.minFloor
                    || request.fromFloor > lift.maxFloor || request.toFloor > lift.maxFloor)
                continue;


            // check if lift can fit requested passengerCount
            if (!lift.canLiftFit(request.passengerCount))
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

    public void showLifts() {
        for (Lift lift : liftsList) {
            System.out.println(lift);
        }
    }

}