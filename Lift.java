import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

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
    Queue<LiftRequest> requests = new ArrayDeque<>();
    Map<Integer, Integer> pickUpRequests = new HashMap<>();
    Map<Integer, Integer> dropOffRequests = new HashMap<>();

    volatile boolean liftRunning = true;

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

    Lift(int liftId, int minFloor, int maxFloor, int totalCapacity){
        this.liftId=liftId;
        this.minFloor=minFloor;
        this.maxFloor=maxFloor;
        this.state=LiftStates.idle;
        this.currentFloor=0;
        this.currentCapacity=0;
        this.totalCapacity=totalCapacity;
    }

    public synchronized void addRequest(LiftRequest newRequest) {
        requests.add(newRequest);
    }

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
            }

            //Handle pickups if lift is idle
            if (!pickUpRequests.isEmpty() && state == LiftStates.idle) {
                int nearestFloor = getNearestFloor(pickUpRequests);
                pickUpPassenger(nearestFloor);
            }

            //Handle drop-offs if no pickups are pending
            if (pickUpRequests.isEmpty() && !dropOffRequests.isEmpty() && state == LiftStates.idle) {
                processRemainingDropOffRequest();
            }

            //Reset to idle if nothing to do
            if (pickUpRequests.isEmpty() && dropOffRequests.isEmpty() && requests.isEmpty()) {
                state = LiftStates.idle;
            }

            try {
                Thread.sleep(200); //simulating time delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

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

    private void processRemainingDropOffRequest() {
        while (!dropOffRequests.isEmpty()) {
            int nearestFloorToTheLift = getNearestFloor(dropOffRequests);
            dropOffPassenger(nearestFloorToTheLift);
        }
    }

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

    private void processRequest(LiftRequest request) {
        // update pickUpRequests
        pickUpRequests.put(request.fromFloor,
                pickUpRequests.getOrDefault(request.fromFloor, 0) + request.passengerCount);

        // update dropOffRequests
        dropOffRequests.put(request.toFloor,
                dropOffRequests.getOrDefault(request.toFloor, 0) + request.passengerCount);
    }

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

    private void checkForDropOffRequests() {
        if (dropOffRequests.containsKey(currentFloor)) {
            int noOfPassengersToDropOff = dropOffRequests.get(currentFloor);
            System.out.println("Dropping off " + noOfPassengersToDropOff +
                    " passengers at " + currentFloor);
            makeLiftThreadWait(boardingTimeMs);
            dropOffRequests.remove(currentFloor); // ✅ remove entry after processing
        }
    }

    private void checkForPickUpRequests() {
        if (pickUpRequests.containsKey(currentFloor)) {
            int noOfPassengersToPickUp = pickUpRequests.get(currentFloor);
            System.out.println("Picking up " + noOfPassengersToPickUp +
                    " passengers at " + currentFloor);
            makeLiftThreadWait(boardingTimeMs);
            pickUpRequests.remove(currentFloor); // ✅ remove entry after processing
        }
    }


    private void moveUp() {
        makeLiftThreadWait(floorChangeTimeMs);
        currentFloor++;
        state = LiftStates.goingUp;
    }

    private void moveDown() {
        makeLiftThreadWait(floorChangeTimeMs);
        currentFloor--;
        state = LiftStates.goingDown;
    }

    public void makeLiftThreadWait(long time){
        try{
            Thread.sleep(time);
        }catch (Exception e){
            System.out.println("Thread technical issue");
        }
    }


    public void stopLift(){
        this.liftRunning=false;
    }

    public void addPassengers(int passengerCount){
        this.currentCapacity+=passengerCount;
    }

    @Override
    public String toString(){
        return "Lift "+this.liftId+" is at "+this.currentFloor+" floor.";
    }

}