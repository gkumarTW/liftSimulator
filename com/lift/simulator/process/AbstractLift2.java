package com.lift.simulator.process;

import com.lift.simulator.exceptions.RequestFloorsOutOfRangeException;
import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.tableUtility.LiftsTableUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;


// making the class abstract as we cannot create object to abstract class
public abstract class AbstractLift2 implements LiftI {

    public final int liftId;
    public final int minFloor;
    public final int maxFloor;
    private int currentFloor;
    protected LiftStates liftState;
    private int currentCapacity;
    private final int totalCapacity;
    protected long floorTravelTimeMs = 3000;
    protected long boardingTimeMs = 2000;

    // below hashmap will hold pick up floor as key and a list of LiftRequest's
    protected Map<Integer, List<LiftRequest>> pickUpRequests = new HashMap<>();

    // below hashmap will hold drop off floor as key and a list of LiftRequest's
    protected Map<Integer, List<LiftRequest>> activeDropOffRequests = new HashMap<>();

    volatile boolean isLiftRunning = true;

    // getter and setter methods
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
    public int getCurrentFloor() {
        return this.currentFloor;
    }
    protected void setCurrentFloor(int currentFloor){
        this.currentFloor=currentFloor;
        this.updateDBCurrentFloor();
    }

    @Override
    public int getCurrentCapacity() {
        return this.currentCapacity;
    }
    protected void setCurrentCapacity(int currentCapacity){
        this.currentCapacity=currentCapacity;
        this.updateDBCurrentCapacity();
    }

    @Override
    public int getTotalCapacity() {
        return this.totalCapacity;
    }

    @Override
    public LiftStates getCurrState() {
        return this.liftState;
    }
    protected void setLiftState(LiftStates liftState){
        this.liftState=liftState;
        this.updateDBLiftState();
    }

    public long getFloorTravelTimeMs(){
        return this.floorTravelTimeMs;
    }
    protected void setFloorTravelTimeMs(long floorTravelTimeMs){
        this.floorTravelTimeMs=floorTravelTimeMs;
    }

    public long getBoardingTimeMs(){
        return this.boardingTimeMs;
    }
    protected void setBoardingTimeMs(long boardingTimeMs){
        this.boardingTimeMs=boardingTimeMs;
    }

    public Map<Integer, List<LiftRequest>> getPickUpRequests() {
        return this.pickUpRequests;
    }

    public Map<Integer, List<LiftRequest>> getActiveDropOffRequests() {
        return this.activeDropOffRequests;
    }

    // assigning initial values when an instance is created for this class
    protected AbstractLift2(int liftId, int minFloor, int maxFloor, int totalCapacity) {
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
        if (!pickUpRequests.isEmpty() && this.getCurrState() == LiftStates.idle) {
            int nearestFloor = findNearestFloor(pickUpRequests.keySet());
            travelTo(nearestFloor, this::checkForPickUpRequests);
        }

        //Handle drop-offs if no pickups are pending
        if (pickUpRequests.isEmpty() && !activeDropOffRequests.isEmpty() && this.getCurrState() == LiftStates.idle) {
            processPendingDropOffs();
        }

        //Reset to idle if nothing to do
        if (pickUpRequests.isEmpty() && activeDropOffRequests.isEmpty()) {
            this.setLiftState(LiftStates.idle);
        }

        //Delaying the thread for data in Collections(requests Queue or Map) to process
        this.makeLiftThreadWait(200);
    }

    // used by com.lift.simulator.process.LiftManager to make the lift process a request
    // main thread comes till here to process a request
    @Override
    public synchronized void addRequest(LiftRequest newRequest) throws
            RequestFloorsOutOfRangeException {
        if (newRequest.getDropOffFloor() < this.getMinFloor() || newRequest.getPickUpFloor() < this.getMinFloor()
                || newRequest.getDropOffFloor() > this.getMaxFloor() || newRequest.getPickUpFloor() > this.getMaxFloor())
            throw new RequestFloorsOutOfRangeException("Requested lift out of buildings range");
        handleRequest(newRequest);
    }

    // this method will spread a utility.LiftRequest into pickUpRequest and dropOffRequest
    protected void handleRequest(LiftRequest newRequest) {
        // update pickUpRequests
        pickUpRequests
                .computeIfAbsent(newRequest.getPickUpFloor(), k -> new ArrayList<>())
                .add(newRequest);

        // update pendingDropOffRequests

    }

    // if there are any pending activeDropOffRequests this method will process them.
    protected void processPendingDropOffs() {
        while (!activeDropOffRequests.isEmpty()) {
            int nearestFloorToTheLift = findNearestFloor(activeDropOffRequests.keySet());
            travelTo(nearestFloorToTheLift, this::checkForDropOffRequests);
        }
    }

    /* This method is used to get the nearest floor to the lift from requests map
     * (either pickUpRequests map or activeDropOffRequests map)
     */
    protected int findNearestFloor(Set<Integer> floors) {
//        int nearestFloorToTheLift = -1;
//        int minDistance = Integer.MAX_VALUE;
//
//        for (Integer currentRequestFloor : requestsMap.keySet()) {
//            //Ensuring the request is within the building's range
//            if (currentRequestFloor >= this.getMinFloor() && currentRequestFloor <= this.getMaxFloor()) {
//                int distance = Math.abs(currentRequestFloor - this.getCurrentFloor());
//                if (distance < minDistance) {
//                    minDistance = distance;
//                    nearestFloorToTheLift = currentRequestFloor;
//                }
//            }
//        }
//        return nearestFloorToTheLift;
        return floors.stream()
                .filter(x->x>=this.getMinFloor() && x<=this.getMaxFloor())//considering only requests within buildings range
                .min(Comparator.comparingInt(x->Math.abs(x-this.getCurrentFloor())))//terminal operation of stream returns a value in the form of Optional object
//              .get()//we can use .get() method to get the value from the optional object returned by .min operation but can throw NoSuchElementFound exception
                .orElse(-1);//It's safer to use .orElse(valueToReturnIfNoSuchElementFound)
    }

    // used to process requests having a target floor(toFloor)
    public void travelTo(int toFloor, Runnable checkExistingRequests) {
        while (this.getCurrentFloor() != toFloor) {
            if (this.getCurrentFloor() < toFloor) {
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
        this.setLiftState(LiftStates.idle);
    }

    // process activeDropOffRequests if any exist in the lift's current floor
    private void checkForDropOffRequests() {
        List<LiftRequest> requests = activeDropOffRequests.get(this.getCurrentFloor());
        if (requests != null) {
            for (LiftRequest request : new ArrayList<>(requests)) {
                removePassengers(request.getPassengerCount());
                System.out.println("Dropping off " + request.getPassengerCount() + " passengers at " + this.getCurrentFloor());
                makeLiftThreadWait(this.getBoardingTimeMs()); // update request status to completed
                request.setStatus(LiftRequestStatus.completed);
                requests.remove(request);
            }
            if (requests.isEmpty()) activeDropOffRequests.remove(this.getCurrentFloor());
        }
    }

    // process pickUpRequests if any exist in the lift's current floor
    private void checkForPickUpRequests() {
        List<LiftRequest> requests = pickUpRequests.get(currentFloor);
        if (requests != null) {
            for (LiftRequest request : new ArrayList<>(requests)) {
                System.out.println("Picking up " + request.getPassengerCount() + " passengers at " + (currentFloor == 0 ? "G" : currentFloor));
                makeLiftThreadWait(boardingTimeMs);
                // update request status to inProgress
                request.setStatus(LiftRequestStatus.inProgress);

                // move to activeDropOffRequests map keyed by dropOffFloor
                if(activeDropOffRequests.containsKey(request.getDropOffFloor())){
                    activeDropOffRequests.get(request.getDropOffFloor()).add(request);
                }else{
                    List<LiftRequest> tempList = new ArrayList<>();
                    tempList.add(request);
                    activeDropOffRequests.put(request.getDropOffFloor(), tempList);
                }

//                    activeDropOffRequests.computeIfAbsent(request.getDropOffFloor(), k -> new ArrayList<>()).add(request);
                requests.remove(request);
            }
            if (requests.isEmpty()) pickUpRequests.remove(currentFloor);
        }
    }

    private void moveUp() {
        makeLiftThreadWait(this.getFloorTravelTimeMs());
        this.setCurrentFloor(this.getCurrentFloor()+1);
        this.setLiftState(LiftStates.goingUp);
    }

    private void moveDown() {
        makeLiftThreadWait(this.getFloorTravelTimeMs());
        this.setCurrentFloor(this.getCurrentFloor()-1);
        this.setLiftState(LiftStates.goingDown);
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
        return this.getCurrentCapacity() + passengerCount <= this.getTotalCapacity();
    }

    public void stopLift() {
        this.isLiftRunning = false;
    }

    public void addPassengers(int passengerCount) {
        this.setCurrentCapacity(this.getCurrentCapacity()+passengerCount);
    }

    public void removePassengers(int passengerCount) {
        this.setCurrentCapacity(this.getCurrentCapacity()-passengerCount);
    }

    private void updateDBCurrentFloor() {
        try(Connection connection = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD)) {
            LiftsTableUtility.updateLiftCurrentFloor(connection, this.getLiftId(), this.currentFloor);
        } catch (Exception e) { System.out.println("Exception occurred: "+e.getMessage()); }
    }

    private void updateDBCurrentCapacity() {
        try(Connection connection = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD)) {
            LiftsTableUtility.updateLiftCurrentCapacity(connection, this.getLiftId(), this.getCurrentCapacity());
        } catch (Exception e) { System.out.println("Exception occurred: "+e.getMessage()); }
    }

    private void updateDBLiftState() {
        try(Connection connection = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD)) {
            LiftsTableUtility.updateLiftState(connection, this.getLiftId(), this.liftState);
        } catch (Exception e) { System.out.println("Exception occurred: "+e.getMessage()); }
    }

    @Override
    public String toString() {
        return "lifts.brands.NormalLift " + this.getLiftId() + " is at " + this.getCurrentFloor() + " floor with "
                + this.getCurrentCapacity() + " passengers and current liftState is " + this.getCurrState()
                + " (" + this.getMinFloor() + ", " + this.getMaxFloor() + ", " + this.getTotalCapacity() + ")";
    }
}