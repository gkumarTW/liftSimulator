package com.lift.simulator.process;

import com.lift.simulator.utility.DBUtility;
import com.lift.simulator.utility.tableUtility.LiftBrandsTableUtility;

import java.sql.SQLException;

public class Lift extends AbstractLift2 {
    private int buildingId;
    private String brand;
    private int brandId;
    private int totalCapacityLimit;

    public int getBuildingId(){
        return this.buildingId;
    }

    public int getBrandId(){
        return this.brandId;
    }

    public String getLiftBrand(){
        return this.brand;
    }

    public int getTotalCapacityLimit(){
        return this.totalCapacityLimit;
    }

    public Lift(int liftId, int minFloor, int maxFloor, int totalCapacity,
                int buildingId, int brandId, int totalCapacityLimit) throws SQLException {
        super(liftId, minFloor, maxFloor, totalCapacity);
        this.buildingId=buildingId;
        this.brand= LiftBrandsTableUtility.getBrandById(DBUtility.getConnection(), brandId);
        this.brandId=brandId;
        this.totalCapacityLimit=totalCapacityLimit;
        this.floorTravelTimeMs = LiftBrandsTableUtility.getFloorTravelTimeMs(DBUtility.getConnection(), brandId);
        this.boardingTimeMs = LiftBrandsTableUtility.getBoardingTimeMs(DBUtility.getConnection(), brandId);
    }

    @Override
    public String toString(){
        return this.getLiftBrand()+" lift " + this.liftId + " is at " + this.getCurrentFloor() + " floor with "
                + this.getCurrentCapacity() + " passengers and current state is " + this.getCurrState()
                + " (" + this.minFloor + ", " + this.maxFloor + ", " + this.getTotalCapacity() + ")";
    }
}
