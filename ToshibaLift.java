public class ToshibaLift extends AbstractLift{
    private final String liftBrand="Toshiba";
    private int totalCapacityLimit=4;

    @Override
    public String getLiftBrand(){
        return this.liftBrand;
    }
    @Override
    public int getTotalCapacityLimit(){
        return this.totalCapacityLimit;
    }

    ToshibaLift(int liftId, int minFloor, int maxFloor, int totalCapacity){
        super(liftId, minFloor,maxFloor, totalCapacity);
        this.floorChangeTimeMs=3500;
        this.boardingTimeMs=1000;
    }

    //Thread method that will carry a loop which makes  thread running
    @Override
    public void run() {
        while (liftRunning) {
            this.processNewRequests();
            this.handlePickUps();
            this.handleDropOffs();
            this.resetToIdle();

            //Delaying the thread for data in Collections(requests Queue or Map) to process
            this.makeLiftThreadWait(200);
        }
    }

    @Override
    public String toString(){
        return this.getLiftBrand()+" lift " + this.liftId + " is at " + this.getCurrentFloor() + " floor with "
                + this.getCurrentCapacity() + " passengers and current state is " + this.getCurrState()
                + " (" + this.minFloor + ", " + this.maxFloor + ", " + this.getTotalCapacity() + ")";
    }
}
