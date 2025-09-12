import java.util.*;

class Lift extends Thread {
    final int liftId;
    final int minFloor;
    final int maxFloor;
    private int currentFloor;
    private LiftStates state;

    private int currentCapacity;
    final private int totalCapacity;

    final long floorChangeTimeMs=3000;
    final long boardingTimeMs=2000;
    private Queue<LiftRequest> requests = new ArrayDeque<>();
    //The below hashmap's will hold key as their floor and passengersCount as their value
    private Map<Integer, Integer> pickUpRequests = new HashMap<>();
    private Map<Integer, Integer> activeDropOffRequests = new HashMap<>();

    //The below hashmap will hold pick up floor as key and a list of pendingDropOffRequests
    private Map<Integer, List<DropOffRequest>> pendingDropOffRequests=new HashMap<>();

    private class DropOffRequest{
        final int dropOffFloor;
        final int passengerCount;
        DropOffRequest(int dropOffFloor, int passengerCount){
            this.dropOffFloor=dropOffFloor;
            this.passengerCount=passengerCount;
        }
    }

    volatile boolean liftRunning = true;

    //get methods for private class variables
    public int getCurrentCapacity(){
        return this.currentCapacity;
    }
    public int getTotalCapacity(){
        return this.totalCapacity;
    }
    public LiftStates getCurrState(){
        return this.state;
    }

    public int getCurrentFloor(){
        return this.currentFloor;
    }

    //Assigning initial values when an instance is created for this class
    Lift(int liftId, int minFloor, int maxFloor, int totalCapacity){
        this.liftId=liftId;
        this.minFloor=minFloor;
        this.maxFloor=maxFloor;
        this.state=LiftStates.idle;
        this.currentFloor=0;
        this.currentCapacity=0;
        this.totalCapacity=totalCapacity;
    }

    //used by LiftManager to make the lift process a request
    public synchronized void addRequest(LiftRequest newRequest) throws
            RequestFloorsOutOfRangeException {
        if(newRequest.toFloor<this.minFloor || newRequest.fromFloor<this.minFloor
                || newRequest.toFloor>this.maxFloor || newRequest.fromFloor>this.maxFloor)
            throw new RequestFloorsOutOfRangeException("Requested lift out of buildings range");
        requests.add(newRequest);
    }

    //Thread method that will carry a loop which makes  thread running
    @Override
    public void run() {
        while (liftRunning) {

            //Process any new requests
            if (!requests.isEmpty()) {
                LiftRequest req;
                synchronized (this) {
                    req = requests.poll();
                }
                if (req != null) {
                    processRequest(req);
                }

                /* The above code is just a safer version to the below code, synchronize will not allow
                 * other threads to access this class until the block is executed
                 *
                 * processRequest(requests.poll());
                 */
            }

            //Handle pickups if lift is idle
            if (!pickUpRequests.isEmpty() && state == LiftStates.idle) {
                int nearestFloor = getNearestFloor(pickUpRequests);
                pickUpPassenger(nearestFloor);
            }

            //Handle drop-offs if no pickups are pending
            if (pickUpRequests.isEmpty() && !activeDropOffRequests.isEmpty() && state == LiftStates.idle) {
                processRemainingDropOffRequest();
            }

            //Reset to idle if nothing to do
            if (pickUpRequests.isEmpty() && activeDropOffRequests.isEmpty() && requests.isEmpty()) {
                state = LiftStates.idle;
            }

            //Delaying the thread for data in Collections(requests Queue or Map) to process
            try {
                Thread.sleep(200); //simulating time delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /* This method is used to get the nearest floor to the lift from requests map
     * (either pickUpRequests map or activeDropOffRequests map)
     */
    private int getNearestFloor(Map<Integer, Integer> requestsMap) {
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

    //This method will spread a LiftRequest into pickUpRequest and dropOffRequest
    private void processRequest(LiftRequest request) {
        // update pickUpRequests
        pickUpRequests.put(request.fromFloor,
                pickUpRequests.getOrDefault(request.fromFloor, 0) + request.passengerCount);

        // update pendingDropOffRequests
        if(pendingDropOffRequests.containsKey(request.fromFloor)){
            List<DropOffRequest> dropOffRequestsList=pendingDropOffRequests.get(request.fromFloor);
            dropOffRequestsList.add(new DropOffRequest(request.toFloor,request.passengerCount));
        }else{
            pendingDropOffRequests.put(request.fromFloor, new ArrayList<>
                    (List.of(new DropOffRequest(request.toFloor, request.passengerCount))));
        }
    }

    //If any there are any pending activeDropOffRequests this method will process them.
    private void processRemainingDropOffRequest() {
        while (!activeDropOffRequests.isEmpty()) {
            int nearestFloorToTheLift = getNearestFloor(activeDropOffRequests);
            dropOffPassenger(nearestFloorToTheLift);
        }
    }

    //MERGE THE BELOW TWO METHODS

    //used to process drop off requests
    private void dropOffPassenger(int dropOffFloor) {
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
    private void pickUpPassenger(int pickUpFloor) {
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
            currentCapacity-=noOfPassengersToDropOff;
        }
    }


    //process pickUpRequests if any exist in the lift's current floor
    private void checkForPickUpRequests() {
        if (pickUpRequests.containsKey(currentFloor)) {
            int noOfPassengersToPickUp = pickUpRequests.get(currentFloor);
            System.out.println("Picking up " + noOfPassengersToPickUp +
                    " passengers at " + (currentFloor==0?'G':currentFloor));
            makeLiftThreadWait(boardingTimeMs);

            //HAVE TO MOVE PENDING DROP OFF REQUESTS TO ACTIVE DROP OFF REQUESTS
            //Thinking to generate a unique id for every request using Date and time.

//            if(pendingDropOffRequests.containsKey(currentFloor)){
//                pendingDropOffRequests.get()
//            }
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
    public void makeLiftThreadWait(long time){
        try{
            Thread.sleep(time);
        }catch (Exception e){
            System.out.println("Thread technical issue");
        }
    }

    //Checks if the lift can fit the passengers or not
    public boolean canLiftFit(int passengerCount){
        return this.currentCapacity+passengerCount <= totalCapacity;
    }

    //End the thread
    public void stopLift(){
        this.liftRunning=false;
    }

    //Increment the passenger count
    public void addPassengers(int passengerCount){
        this.currentCapacity+=passengerCount;
    }

    //to use the class Lift for SOP
    @Override
    public String toString(){
        return "Lift "+this.liftId+" is at "+this.currentFloor+" floor with "
                +this.currentCapacity+" passengers and current state is "+ this.state+" ("+this.minFloor+ ", "+this.maxFloor+ ", "+this.totalCapacity+")";
    }

}