import java.util.HashMap;
import java.util.Map;

public class LiftManager{
    private int maxFloors;
    private Map<Integer, Lift> liftsMap;

    LiftManager(int maxFloors, Lift... lifts){
        this.maxFloors=maxFloors;
        this.liftsMap=new HashMap<>();
        for(Lift lift: lifts){
            this.liftsMap.put(lift.liftId,lift);
        }
    }

    public int handleLiftRequest(LiftRequest request){
        if(request.getFromFloor()<0 || request.getFromFloor()>maxFloors
                || request.getToFloor()<0
                || request.getToFloor()>maxFloors){
            throw new InvalidFloorException();
        }

        //Have to handle null value response from findNearestLift method
        Lift nearestLift=findNearestLift(request.getFromFloor(),liftsMap);
//        nearestLift.requestLift(request);
        nearestLift.addDestination(request.getFromFloor());
//        nearestLift.addDestination(request.getToFloor());
        return nearestLift.liftId;
    }


    public Lift findNearestLift(int fromFloor, Map<Integer, Lift> lifts){
        int minDistance=Integer.MAX_VALUE;
        Lift nearestLift=null;
        for(Lift lift:lifts.values()){
            int currDistance=LiftUtilityMethods.calculateDistance(maxFloors,fromFloor,lift.getCurrentFloor(),lift.state);
            if(currDistance<minDistance){
                minDistance=currDistance;
                nearestLift=lift;
            }
        }
        return nearestLift;
    }

    public void startLifts(){
        for (Lift lift: liftsMap.values()){
            new Thread(lift).start();
        }
    }

    public void stopLifts(){
        for(Lift lift : liftsMap.values()){
            lift.stopLift();
        }
    }

}