package lifts;

import exception.RequestFloorsOutOfRangeException;
import utility.DropOffRequest;
import lifts.LiftRequest;

import java.util.*;


// making the class abstract as we cannot create object to abstract class
public abstract class AbstractLift implements LiftI {
    public final int liftId;
    public final int minFloor;
    public final int maxFloor;
    private int currentFloor;
    protected LiftStates liftState;

    private int currentCapacity;
    private final int totalCapacity;

    protected long floorTravelTimeMs = 3000;
    protected long boardingTimeMs = 2000;

    /*Thought:  THE BELOW HASHMAPS CAN SIMPLY HOLD THE utility.LiftRequest ITSELF AS VALUE, IN ORDER TO COMPLETELY
     *          REMOVE THE USAGE OF pendingDropOffRequests MAP
     */

    // below hashmap's will hold key as their floor and passengersCount as their value

    // below hashmap will hold pick up floor as key and a list of pendingDropOffRequests
    protected Map<Integer, List<DropOffRequest>> pendingDropOffRequests = new HashMap<>();


    protected Map<Integer, Integer> pickUpRequests = new HashMap<>();
    protected Map<Integer, Integer> activeDropOffRequests = new HashMap<>();
    volatile boolean isLiftRunning = true;

    //getter methods
    @Override
    public int getLiftId() {
        return this.liftId;
    }
    @Override
    public int getMinFloor() {
        return this.minFloor;
    }
    @Override
    public int getMaxFloor() {
        return this.maxFloor;
    }
    @Override
    public int getCurrentCapacity() {
        return this.currentCapacity;
    }
    @Override
    public int getTotalCapacity() {
        return this.totalCapacity;
    }
    @Override
    public LiftStates getCurrState() {
        return this.liftState;
    }
    @Override
    public int getCurrentFloor() {
        return this.currentFloor;
    }

    public Map<Integer, Integer> getActiveDropOffRequests() {
        return this.activeDropOffRequests;
    }
    public Map<Integer, Integer> getPickUpRequests() {
        return this.pickUpRequests;
    }

    // assigning initial values when an instance is created for this class
    protected AbstractLift(int liftId, int minFloor, int maxFloor, int totalCapacity) {
        this.liftId = liftId;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.liftState = LiftStates.idle;
        this.currentFloor = 0;
        this.currentCapacity = 0;
        this.totalCapacity = totalCapacity;
    }

    @Override
    public void run() {
        while (isLiftRunning) {
            executeLiftCycle();
        }
    }

    // holds the logic to process requests and handles lift movements
    protected void executeLiftCycle() {
        //Handle pickups if lift is idle
        if (!pickUpRequests.isEmpty() && liftState == LiftStates.idle) {
            int nearestFloor = findNearestFloor(pickUpRequests);
            travelTo(nearestFloor, this::checkForPickUpRequests);
        }

        //Handle drop-offs if no pickups are pending
        if (pickUpRequests.isEmpty() && !activeDropOffRequests.isEmpty() && liftState == LiftStates.idle) {
            processPendingDropOffs();
        }

        //Reset to idle if nothing to do
        if (pickUpRequests.isEmpty() && activeDropOffRequests.isEmpty()) {
            liftState = LiftStates.idle;
        }

        //Delaying the thread for data in Collections(requests Queue or Map) to process
        this.makeLiftThreadWait(200);
    }

    // used by lifts.LiftManager to make the lift process a request
    // main thread comes till here to process a request
    @Override
    public synchronized void addRequest(LiftRequest newRequest) throws
            RequestFloorsOutOfRangeException {
        if (newRequest.getDropOffFloor() < this.minFloor || newRequest.getPickUpFloor() < this.minFloor
                || newRequest.getDropOffFloor() > this.maxFloor || newRequest.getPickUpFloor() > this.maxFloor)
            throw new RequestFloorsOutOfRangeException("Requested lift out of buildings range");
        handleRequest(newRequest);
    }

    // this method will spread a utility.LiftRequest into pickUpRequest and dropOffRequest
    protected void handleRequest(LiftRequest request) {
        // update pickUpRequests
        pickUpRequests.put(request.getPickUpFloor(),
                pickUpRequests.getOrDefault(request.getPickUpFloor(), 0) + request.getPassengerCount());

        // update pendingDropOffRequests
        if (pendingDropOffRequests.containsKey(request.getPickUpFloor())) {
            List<DropOffRequest> dropOffRequestsList = pendingDropOffRequests.get(request.getPickUpFloor());
            dropOffRequestsList.add(new DropOffRequest(request.getDropOffFloor(), request.getPassengerCount()));
        } else {
            pendingDropOffRequests.put(request.getPickUpFloor(), new ArrayList<>
                    (List.of(new DropOffRequest(request.getDropOffFloor(), request.getPassengerCount()))));
        }
    }

    // if there are any pending activeDropOffRequests this method will process them.
    protected void processPendingDropOffs() {
        while (!activeDropOffRequests.isEmpty()) {
            int nearestFloorToTheLift = findNearestFloor(activeDropOffRequests);
            travelTo(nearestFloorToTheLift, this::checkForDropOffRequests);
        }
    }

    /* This method is used to get the nearest floor to the lift from requests map
     * (either pickUpRequests map or activeDropOffRequests map)
     */
    protected int findNearestFloor(Map<Integer, Integer> requestsMap) {
//        int nearestFloorToTheLift = -1;
//        int minDistance = Integer.MAX_VALUE;
//
//        for (Integer currentRequestFloor : requestsMap.keySet()) {
//            //Ensuring the request is within the building's range
//            if (currentRequestFloor >= this.minFloor && currentRequestFloor <= this.maxFloor) {
//                int distance = Math.abs(currentRequestFloor - currentFloor);
//                if (distance < minDistance) {
//                    minDistance = distance;
//                    nearestFloorToTheLift = currentRequestFloor;
//                }
//            }
//        }
//        return nearestFloorToTheLift;
        return requestsMap.keySet().stream()
                .filter(x->x>=this.minFloor && x<=this.maxFloor)//considering only requests within buildings range
                .min(Comparator.comparingInt(x->Math.abs(x-this.currentFloor)))//terminal operation of stream returns a value in the form of Optional object
//              .get()//we can use .get() method to get the value from the optional object returned by .min operation but can throw NoSuchElementFound exception
                .orElse(-1);//It's safer to use .orElse(valueToReturnIfNoSuchElementFound)
    }

    // used to process requests having a target floor(toFloor)
    public void travelTo(int toFloor, Runnable checkExistingRequests) {
        while (currentFloor != toFloor) {
            if (currentFloor < toFloor) {
                moveUp();
            } else {
                moveDown();
            }
            // check for new requests(if found process them)
            checkForPickUpRequests();
            checkForDropOffRequests();
        }

        // reached floor → drop passengers
        checkExistingRequests.run();
        liftState = LiftStates.idle;
    }

    // process activeDropOffRequests if any exist in the lift's current floor
    private void checkForDropOffRequests() {
        if (activeDropOffRequests.containsKey(currentFloor)) {
            int noOfPassengersToDropOff = activeDropOffRequests.get(currentFloor);
            System.out.println("Dropping off " + noOfPassengersToDropOff +
                    " passengers at " + currentFloor);
            makeLiftThreadWait(boardingTimeMs);
            activeDropOffRequests.remove(currentFloor); // remove entry after processing
            currentCapacity -= noOfPassengersToDropOff;
        }
    }

    // process pickUpRequests if any exist in the lift's current floor
    private void checkForPickUpRequests() {
        if (pickUpRequests.containsKey(currentFloor)) {
            int noOfPassengersToPickUp = pickUpRequests.get(currentFloor);
            System.out.println("Picking up " + noOfPassengersToPickUp +
                    " passengers at " + (currentFloor == 0 ? "G" : currentFloor));
            makeLiftThreadWait(boardingTimeMs);

            //HAVE TO MOVE PENDING DROP OFF REQUESTS TO ACTIVE DROP OFF REQUESTS

            if (pendingDropOffRequests.containsKey(this.currentFloor)) {
                List<DropOffRequest> pendingDropOffRequestsList = pendingDropOffRequests.get(this.currentFloor);
                for (DropOffRequest dropOffRequest : pendingDropOffRequestsList) {
                    activeDropOffRequests.put(dropOffRequest.dropOffFloor,
                            activeDropOffRequests.getOrDefault
                                    (dropOffRequest.dropOffFloor, 0) + dropOffRequest.passengerCount);
                }
                pendingDropOffRequests.remove(this.currentFloor);
            }
            pickUpRequests.remove(currentFloor); // remove entry after processing
        }

    }

    private void moveUp() {
        makeLiftThreadWait(floorTravelTimeMs);
        currentFloor++;
        liftState = LiftStates.goingUp;
    }

    private void moveDown() {
        makeLiftThreadWait(floorTravelTimeMs);
        currentFloor--;
        liftState = LiftStates.goingDown;
    }

    /* Used to make the thread sleep for some time(replicating time taken
     * for lift to cross a floor, time taken for the passenger to board the lift)
     */
    public void makeLiftThreadWait(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            System.out.println("Thread technical issue");
        }
    }

    public boolean canLiftFit(int passengerCount) {
        return this.currentCapacity + passengerCount <= totalCapacity;
    }

    public void stopLift() {
        this.isLiftRunning = false;
    }

    public void addPassengers(int passengerCount) {
        this.currentCapacity += passengerCount;
    }

    @Override
    public String toString() {
        return "lifts.brands.NormalLift " + this.liftId + " is at " + this.currentFloor + " floor with "
                + this.currentCapacity + " passengers and current liftState is " + this.liftState
                + " (" + this.minFloor + ", " + this.maxFloor + ", " + this.totalCapacity + ")";
    }
}