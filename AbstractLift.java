import java.util.*;


//Making the class abstract as we cannot create object to abstract class
abstract class AbstractLift implements LiftI {
    final int liftId;
    final int minFloor;
    final int maxFloor;
    private int currentFloor;
    protected LiftStates state;

    private int currentCapacity;
    final private int totalCapacity;

    protected long floorChangeTimeMs = 3000;
    protected long boardingTimeMs = 2000;
    protected Queue<LiftRequest> requests = new ArrayDeque<>();

    /* THE BELOW HASHMAP'S CAN SIMPLY HOLD THE LiftRequest ITSELF AS VALUE, IN ORDER TO COMPLETELY
     * REMOVE THE USAGE OF pendingDropOffRequests MAP
     */

    //The below hashmap's will hold key as their floor and passengersCount as their value
    protected Map<Integer, Integer> pickUpRequests = new HashMap<>();
    protected Map<Integer, Integer> activeDropOffRequests = new HashMap<>();

    //The below hashmap will hold pick up floor as key and a list of pendingDropOffRequests
    protected Map<Integer, List<DropOffRequest>> pendingDropOffRequests = new HashMap<>();

    private class DropOffRequest {
        final int dropOffFloor;
        final int passengerCount;

        DropOffRequest(int dropOffFloor, int passengerCount) {
            this.dropOffFloor = dropOffFloor;
            this.passengerCount = passengerCount;
        }
    }

    volatile boolean liftRunning = true;

    //get methods for private class variables
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
        return this.state;
    }
    public void setCurrState(LiftStates state){
        this.state=state;
    }
    @Override
    public int getCurrentFloor() {
        return this.currentFloor;
    }
    @Override
    public Queue<LiftRequest> getRequests() {
        return this.requests;
    }
    @Override
    public Map<Integer, Integer> getActiveDropOffRequests(){
        return this.activeDropOffRequests;
    }
    @Override
    public Map<Integer, Integer> getPickUpRequests(){
        return this.pickUpRequests;
    }

    //Assigning initial values when an instance is created for this class
    AbstractLift(int liftId, int minFloor, int maxFloor, int totalCapacity) {
        this.liftId = liftId;
        this.minFloor = minFloor;
        this.maxFloor = maxFloor;
        this.state = LiftStates.idle;
        this.currentFloor = 0;
        this.currentCapacity = 0;
        this.totalCapacity = totalCapacity;
    }

    //used by LiftManager to make the lift process a request
    @Override
    public synchronized void addRequest(LiftRequest newRequest) throws
            RequestFloorsOutOfRangeException {
        if (newRequest.toFloor < this.minFloor || newRequest.fromFloor < this.minFloor
                || newRequest.toFloor > this.maxFloor || newRequest.fromFloor > this.maxFloor)
            throw new RequestFloorsOutOfRangeException("Requested lift out of buildings range");
        requests.add(newRequest);
        processRequest(newRequest);
    }

    //This method will spread a LiftRequest into pickUpRequest and dropOffRequest
    protected void processRequest(LiftRequest request) {
        // update pickUpRequests
        pickUpRequests.put(request.fromFloor,
                pickUpRequests.getOrDefault(request.fromFloor, 0) + request.passengerCount);

        // update pendingDropOffRequests
        if (pendingDropOffRequests.containsKey(request.fromFloor)) {
            List<DropOffRequest> dropOffRequestsList = pendingDropOffRequests.get(request.fromFloor);
            dropOffRequestsList.add(new DropOffRequest(request.toFloor, request.passengerCount));
        } else {
            pendingDropOffRequests.put(request.fromFloor, new ArrayList<>
                    (List.of(new DropOffRequest(request.toFloor, request.passengerCount))));
        }
    }

    //If any there are any pending activeDropOffRequests this method will process them.
    protected void processRemainingDropOffRequest() {
        while (!activeDropOffRequests.isEmpty()) {
            int nearestFloorToTheLift = getNearestFloor(activeDropOffRequests);
            dropOffPassenger(nearestFloorToTheLift);
        }
    }


    /* This method is used to get the nearest floor to the lift from requests map
     * (either pickUpRequests map or activeDropOffRequests map)
     */
    protected int getNearestFloor(Map<Integer, Integer> requestsMap) {
        int nearestFloorToTheLift = -1;
        int minDistance = Integer.MAX_VALUE;

        for (Integer currentRequestFloor : requestsMap.keySet()) {
            if (currentRequestFloor >= minFloor && currentRequestFloor <= maxFloor) {
                int distance = Math.abs(currentRequestFloor - currentFloor);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestFloorToTheLift = currentRequestFloor;
                }
            }
        }
        return nearestFloorToTheLift;
    }

    //MERGE THE BELOW TWO METHODS

    //used to process drop off requests
    protected void dropOffPassenger(int dropOffFloor) {
        while (currentFloor != dropOffFloor) {
            if (currentFloor < dropOffFloor) {
                moveUp();
            } else {
                moveDown();
            }
            checkForPickUpRequests();
            checkForDropOffRequests();
        }

        // reached floor → drop passengers
        checkForDropOffRequests();
        state = LiftStates.idle;
    }

    //used to process pick up requests
    protected void pickUpPassenger(int pickUpFloor) {
        while (currentFloor != pickUpFloor) {
            if (currentFloor < pickUpFloor) {
                moveUp();
            } else {
                moveDown();
            }
            checkForPickUpRequests();
            checkForDropOffRequests();
        }

        // reached floor → pick passengers
        checkForPickUpRequests();
        state = LiftStates.idle;
    }


    //process activeDropOffRequests if any exist in the lift's current floor
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


    //process pickUpRequests if any exist in the lift's current floor
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

    //make the lift move up
    private void moveUp() {
        makeLiftThreadWait(floorChangeTimeMs);
        currentFloor++;
        state = LiftStates.goingUp;
    }

    //make the lift move down
    private void moveDown() {
        makeLiftThreadWait(floorChangeTimeMs);
        currentFloor--;
        state = LiftStates.goingDown;
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

    //Checks if the lift can fit the passengers or not
    public boolean canLiftFit(int passengerCount) {
        return this.currentCapacity + passengerCount <= totalCapacity;
    }

    //End the thread
    public void stopLift() {
        this.liftRunning = false;
    }

    //Increment the passenger count
    public void addPassengers(int passengerCount) {
        this.currentCapacity += passengerCount;
    }

    //to use the class Lift for SOP
    @Override
    public String toString() {
        return "Lift " + this.liftId + " is at " + this.currentFloor + " floor with "
                + this.currentCapacity + " passengers and current state is " + this.state
                + " (" + this.minFloor + ", " + this.maxFloor + ", " + this.totalCapacity + ")";
    }

}