import java.util.ArrayDeque;
import java.util.Deque;

public class Lift extends Thread{
    public final int liftId;
    public LiftState state;
    private final int minFloors;
    private final int maxFloors;
    private int currentFloor;
    private int passengerCount;
    private final int capacity=6;
    private Deque<Integer> destinationRequests=new ArrayDeque<>();

    private Deque<Integer> destinations=new ArrayDeque<>();

    private final long floorTravelTimeMs=3000;
    private final long waitForPassengerMs=1000;

    private volatile boolean running;

    public int getCurrentFloor(){
        return this.currentFloor;
    }

    public Lift(int liftId, int maxFloors, int minFloors){
        this.liftId=liftId;
        this.state=LiftState.idle;
        this.minFloors=minFloors;
        this.maxFloors=maxFloors;
        this.currentFloor=0;
        this.passengerCount=0;
        this.running=true;
    }

    public void run(){
        while(running){
            if(!destinationRequests.isEmpty()){
                System.out.println("Destinations size= "+destinations.size());
                int destination=destinations.pop();
                destinations.addLast(destination);
                if(this.state==LiftState.goingUp && destination<this.currentFloor){
//                    goToDestination(maxFloors);
                }
//                this.goToDestination(destination);
            }
        }
    }

    public void goToDestination(int destination){
        int distanceToDestination=LiftUtilityMethods.calculateDistance(maxFloors,destination,this.currentFloor,this.state);

        System.out.println("MAKING THE LIFT GO TO: "+distanceToDestination);
        int coveredDistance=0;

        if(destination<this.currentFloor)
            this.state=LiftState.goingDown;
        else
            this.state=LiftState.goingUp;
        while(distanceToDestination-->0){

            this.threadWait(this.floorTravelTimeMs);

            coveredDistance++;
            if(this.state==LiftState.goingDown){
                this.currentFloor--;
            }else{
                this.currentFloor++;
            }

            //If any later requests involve lifts current floors as destination.
            if(destinations.contains(this.currentFloor)){
                this.threadWait(this.waitForPassengerMs);
                destinations.remove(this.currentFloor);
                this.liftArrivedAt(this.currentFloor);
            }
        }

        //Setting the state of lift to "idle" once all the requests are processed
        if(destinations.isEmpty()){
            this.state=LiftState.idle;
        }
    }

    public void liftArrivedAt(int floor){
//        System.out.println("LIFT "+this.liftId+" ARRIVED AT "+floor);
    }

    public void threadWait(long time){
        try{
            Thread.sleep(time);
        }catch(InterruptedException e){
            System.out.println("Thread Interrupted");
        }
    }


    public void addDestination(int destination){
        this.passengerCount++;
        destinationRequests.addLast(destination);
    }

    public void stopLift(){
        this.running=false;
    }

    @Override
    public String toString(){
        return "Lift id: "+ liftId +", currentFloor: "+currentFloor+", state " +state;
    }
}