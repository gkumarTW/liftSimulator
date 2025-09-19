package lifts.brands;

import lifts.AbstractLift;

public class NormalLift extends AbstractLift {
    private final String liftBrand="Normal";
    private int totalCapacityLimit=10;

    public String getLiftBrand(){
        return this.liftBrand;
    }
    public int getTotalCapacityLimit() {
        return this.totalCapacityLimit;
    }

    public NormalLift(int liftId, int minFloor, int maxFloor, int totalCapacity){
        super(liftId,minFloor,maxFloor,totalCapacity);
        this.floorTravelTimeMs=2000;
        this.boardingTimeMs=500;
    }

    //Thread method that will carry a loop which makes thread running
    /* @Override
     * public void run() {}
     */

    @Override
    public String toString(){
        return this.getLiftBrand()+" lift " + this.liftId + " is at " + this.getCurrentFloor() + " floor with "
                + this.getCurrentCapacity() + " passengers and current state is " + this.getCurrState()
                + " (" + this.minFloor + ", " + this.maxFloor + ", " + this.getTotalCapacity() + ")";
    }
}
