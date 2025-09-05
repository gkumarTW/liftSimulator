import java.util.ArrayDeque;
import java.util.Deque;

class Lift extends Thread{
    public final int liftId;
    public LiftState state;
    private final int maxFloors;
    private int currentFloor;
    private int passengerCount;
    private final int capacity=6;
    private Deque<LiftRequest> pendingRequests=new ArrayDeque<>();
    private final long floorTravelTimeMs=3000;

    private volatile boolean running;

    public int getCurrentFloor(){
        return this.currentFloor;
    }

    public Lift(int liftId, int maxFloors){
        this.liftId=liftId;
        this.state=LiftState.idle;
        this.maxFloors=maxFloors;
        this.currentFloor=1;
        this.passengerCount=0;
        this.running=true;
    }

    public void run(){
        while(running){
            if(!pendingRequests.isEmpty()){
                LiftRequest request=pendingRequests.pop();

                int distanceFromLiftToUser=
                        LiftUtilityMethods.calculateDistance(maxFloors,request.getFromFloor(), currentFloor, state);
                int distanceUserToDestination=Math.abs(request.getToFloor()-request.getFromFloor());

                int totalDistanceToCover=distanceFromLiftToUser+distanceUserToDestination;
                int coveredDistance=0;

                if(request.getFromFloor()<this.currentFloor)
                    this.state=LiftState.goingDown;
                else
                    this.state=LiftState.goingUp;

                while(distanceFromLiftToUser-->0){
                    coveredDistance++;
                    if(this.state==LiftState.goingDown){
                        this.currentFloor--;
                    }else{
                        this.currentFloor++;
                    }
                    try{
                        Thread.sleep(this.floorTravelTimeMs);
                    }catch(InterruptedException e){
                        System.out.println("Thread Interrupted");
                    }
                }

                if(request.getToFloor()<this.currentFloor)
                    this.state=LiftState.goingDown;
                else
                    this.state=LiftState.goingUp;

                while(distanceUserToDestination-->0){
                    coveredDistance++;
                    if(this.state==LiftState.goingDown){
                        this.currentFloor--;
                    }else{
                        this.currentFloor++;
                    }
                    try{
                        Thread.sleep(this.floorTravelTimeMs);
                    }catch(InterruptedException e){
                        System.out.println("Thread Interrupted");
                    }
                }


                System.out.println();
//                System.out.println("DISTANCE---------------"+distanceFromLiftToUser+" "+distanceUserToDestination);
                System.out.println("**LIFT "+this.liftId+" HAS ARRIVED AT "+request.getToFloor()+"th FLOOR IN: "+
                        (coveredDistance*this.floorTravelTimeMs)/1000+" SECONDS**");

                if(pendingRequests.size()==0){
                    this.state=LiftState.idle;
                }
            }
        }
    }

    public void requestLift(LiftRequest newRequest){
        this.passengerCount++;
        pendingRequests.add(newRequest);
//        System.out.println("Your request from "+newRequest.getFromFloor()+" to "
//                +newRequest.getToFloor()+" has been registered.");
    }

    public void stopLift(){
        this.running=false;
    }

    @Override
    public String toString(){
        return "Lift id: "+ liftId +", currentFloor: "+currentFloor+", state " +state;
    }
}