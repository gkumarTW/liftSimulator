package lifts;

import exception.RequestFloorsOutOfRangeException;
import utility.DBConstants;
import lifts.LiftRequest;
import lifts.LiftRequestStatus;
import utility.tableUtility.LiftsTableUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// making the class abstract as we cannot create object to abstract class
public abstract class AbstractLift2 implements LiftI, Runnable {

    public final int liftId;
    public final int minFloor;
    public final int maxFloor;
    private int currentFloor;
    protected LiftStates liftState;

    private int currentCapacity;
    private final int totalCapacity;

    protected long floorTravelTimeMs = 3000;
    protected long boardingTimeMs = 2000;

    volatile boolean isLiftRunning = true;

    /*Thought:  pickUpRequests map now stores List<LiftRequest> as value instead of single LiftRequest
     *          activeDropOffRequests map stores List<LiftRequest> as value instead of single LiftRequest
     *          This allows multiple requests from the same floor
     */
    protected Map<Integer, List<LiftRequest>> pickUpRequests = new ConcurrentHashMap<>();
    protected Map<Integer, List<LiftRequest>> activeDropOffRequests = new ConcurrentHashMap<>();

    //getter methods
    @Override
    public int getLiftId() { return this.liftId; }

    @Override
    public int getMinFloor() { return this.minFloor; }

    @Override
    public int getMaxFloor() { return this.maxFloor; }

    @Override
    public int getCurrentCapacity() { return this.currentCapacity; }

    @Override
    public int getTotalCapacity() { return this.totalCapacity; }

    @Override
    public LiftStates getCurrState() { return this.liftState; }

    @Override
    public int getCurrentFloor() { return this.currentFloor; }

    // setter methods
    protected void setCurrentFloor(int currentFloor) {
        this.currentFloor = currentFloor;
        updateDBCurrentFloor();
    }

    protected void setLiftState(LiftStates liftState) {
        this.liftState = liftState;
        updateDBLiftState();
    }

    protected void setCurrentCapacity(int currentCapacity) {
        this.currentCapacity = currentCapacity;
        updateDBCurrentCapacity();
    }

    protected void incrementCurrentFloor() { setCurrentFloor(this.currentFloor + 1); }

    protected void decrementCurrentFloor() { setCurrentFloor(this.currentFloor - 1); }

    protected void incrementCurrentCapacity(int value) { setCurrentCapacity(this.currentCapacity + value); }

    protected void decrementCurrentCapacity(int value) { setCurrentCapacity(this.currentCapacity - value); }

    protected void setLiftRunning(boolean isRunning) { this.isLiftRunning = isRunning; }

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
        if (!pickUpRequests.isEmpty() && liftState == LiftStates.idle) {
            int nearestFloor = findNearestFloor(pickUpRequests.keySet());
            travelTo(nearestFloor, this::checkForPickUpRequests);
        }

        //After pickup, handle drop-offs if any became active
        if (!activeDropOffRequests.isEmpty()) {
            int nearestDropOff = findNearestFloor(activeDropOffRequests.keySet());
            travelTo(nearestDropOff, this::checkForDropOffRequests);
        }

        //Reset to idle if nothing to do
        if (pickUpRequests.isEmpty() && activeDropOffRequests.isEmpty()) {
            setLiftState(LiftStates.idle);
        }

        //Delaying the thread for data in Collections(requests Queue or Map) to process
        this.makeLiftThreadWait(200);
    }

    // used by lifts.LiftManager to make the lift process a request
    // main thread comes till here to process a request
    @Override
    public synchronized void addRequest(LiftRequest newRequest) throws
            RequestFloorsOutOfRangeException {
        if (newRequest.getPickUpFloor() < this.minFloor || newRequest.getDropOffFloor() < this.minFloor
                || newRequest.getPickUpFloor() > this.maxFloor || newRequest.getDropOffFloor() > this.maxFloor)
            throw new RequestFloorsOutOfRangeException("Requested lift out of buildings range");

        System.out.println("Hiiii");
        // add request to pickUpRequests map keyed by pickUpFloor
        pickUpRequests.computeIfAbsent(newRequest.getPickUpFloor(), k -> new ArrayList<>()).add(newRequest);
        System.out.println("Byeee");
    }

    /* This method is used to get the nearest floor to the lift from a set of floors */
    protected int findNearestFloor(Set<Integer> floorSet) {
        return floorSet.stream()
                .filter(f -> f >= this.minFloor && f <= this.maxFloor)
                .min(Comparator.comparingInt(f -> Math.abs(f - this.currentFloor)))
                .orElse(-1);
    }

    // process activeDropOffRequests if any exist in the lift's current floor
    private void checkForDropOffRequests() {
        List<LiftRequest> requests = activeDropOffRequests.get(currentFloor);
        if (requests != null) {
            for (LiftRequest request : new ArrayList<>(requests)) {
                decrementCurrentCapacity(request.getPassengerCount());
                System.out.println("Dropping off " + request.getPassengerCount() + " passengers at " + currentFloor);
                makeLiftThreadWait(boardingTimeMs);

                // update request status to completed
                request.setStatus(LiftRequestStatus.completed);

                requests.remove(request);
            }
            if (requests.isEmpty()) activeDropOffRequests.remove(currentFloor);
        }
    }

    // process pickUpRequests if any exist in the lift's current floor
    private void checkForPickUpRequests() {
        List<LiftRequest> requests = pickUpRequests.get(currentFloor);
        if (requests != null && !requests.isEmpty()) {
            Iterator<LiftRequest> iterator = requests.iterator();
            while (iterator.hasNext()) {
                LiftRequest request = iterator.next();
                if (canLiftFit(request.getPassengerCount())) {
//                incrementCurrentCapacity(request.getPassengerCount());
                    System.out.println("Picking up " + request.getPassengerCount() + " passengers at "
                            + (currentFloor == 0 ? "G" : currentFloor));
                    makeLiftThreadWait(boardingTimeMs);

                    // update request status to inProgress
                    request.setStatus(LiftRequestStatus.inProgress);

                    // move to activeDropOffRequests map keyed by dropOffFloor
                    activeDropOffRequests.computeIfAbsent(request.getDropOffFloor(),
                            k -> new ArrayList<>()).add(request);

                    iterator.remove();
                }
            }

            if (requests.isEmpty()) pickUpRequests.remove(currentFloor);
        }
    }

    public void travelTo(int toFloor, Runnable checkExistingRequests) {
        while (currentFloor != toFloor && isLiftRunning) {
            // dynamically re-evaluate if new closer pickup requests came while travelling
            if (!pickUpRequests.isEmpty()) {
                int nearestPickUp = findNearestFloor(pickUpRequests.keySet());
                if (nearestPickUp != -1 && nearestPickUp != toFloor &&
                        Math.abs(nearestPickUp - currentFloor) < Math.abs(toFloor - currentFloor)) {
                    toFloor = nearestPickUp;
                }
            }

            if (currentFloor < toFloor) moveUp();
            else if (currentFloor > toFloor) moveDown();

            // at every floor, check for both pickups and drop-offs
            checkForPickUpRequests();
            checkForDropOffRequests();
        }

        // reached floor → drop/pick passengers
        checkForPickUpRequests();
        checkForDropOffRequests();

        // if still have pending requests, remain active; else idle
        if (pickUpRequests.isEmpty() && activeDropOffRequests.isEmpty()) {
            setLiftState(LiftStates.idle);
        }
    }

    private void moveUp() {
        makeLiftThreadWait(floorTravelTimeMs);
        incrementCurrentFloor();
        setLiftState(LiftStates.goingUp);
    }

    private void moveDown() {
        makeLiftThreadWait(floorTravelTimeMs);
        decrementCurrentFloor();
        setLiftState(LiftStates.goingDown);
    }

    /* Used to make the thread sleep for some time(replicating time taken
     * for lift to cross a floor, time taken for the passenger to board the lift)
     */
    public void makeLiftThreadWait(long time) {
        try { Thread.sleep(time); } catch (Exception e) { System.out.println("Thread technical issue"); }
    }

    public boolean canLiftFit(int passengerCount) {
        return this.currentCapacity + passengerCount <= totalCapacity;
    }

    public void stopLift() { setLiftRunning(false); }

    public void addPassengers(int passengerCount) { incrementCurrentCapacity(passengerCount); }

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
        return "lifts.brands.NormalLift " + this.liftId + " is at " + this.currentFloor + " floor with "
                + this.currentCapacity + " passengers and current liftState is " + this.liftState
                + " (" + this.minFloor + ", " + this.maxFloor + ", " + this.totalCapacity + ")";
    }
}

