public class LiftUtilityMethods {
    public static int calculateDistance(int totalFloorsInBuilding, int passengerCurrentFloor, int liftCurrentFloor, LiftState liftState){
        totalFloorsInBuilding++;
        passengerCurrentFloor++;
        liftCurrentFloor++;
        int distance;
        int distanceBetweenLiftToPassenger=passengerCurrentFloor-liftCurrentFloor;
        boolean liftGoingTowardsPassenger=(distanceBetweenLiftToPassenger<0 && (liftState==LiftState.goingDown || liftState==LiftState.idle)) ||
                (distanceBetweenLiftToPassenger>0 && (liftState==LiftState.goingUp || liftState==LiftState.idle));
        if(liftState==LiftState.idle || liftGoingTowardsPassenger){
            distance=Math.abs(distanceBetweenLiftToPassenger);
        }else{
            distance=(totalFloorsInBuilding-liftCurrentFloor)+(totalFloorsInBuilding-passengerCurrentFloor);
        }
        return distance;
    }
}
