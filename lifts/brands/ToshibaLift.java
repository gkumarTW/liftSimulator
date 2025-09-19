package lifts.brands;

import lifts.AbstractLift;

public class ToshibaLift extends AbstractLift {
    private final String liftBrand="Toshiba";
    private int totalCapacityLimit=4;

    public String getLiftBrand(){
        return this.liftBrand;
    }
    public int getTotalCapacityLimit(){
        return this.totalCapacityLimit;
    }

    public ToshibaLift(int liftId, int minFloor, int maxFloor, int totalCapacity){
        super(liftId, minFloor,maxFloor, totalCapacity);
        this.floorTravelTimeMs=3500;
        this.boardingTimeMs=1000;
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
