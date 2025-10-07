package lifts;

import utility.DBConstants;
import utility.tableUtility.LiftRequestsTableUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class LiftRequest {
    private int id;
    private int liftId;
    private int pickUpFloor;
    private int dropOffFloor;
    private int passengerCount;
    private LiftRequestStatus status;

    // Getter and Setter methods
    public int getId(){
        return this.id;
    }
    private void setId(int id){
        this.id=id;
    }

    public int getLiftId(){
        return this.liftId;
    }
    private void setLiftId(int liftId){
        this.liftId=liftId;
    }

    public int getPickUpFloor(){
        return this.pickUpFloor;
    }
    private void setPickUpFloor(int pickUpFloor){
        this.pickUpFloor=pickUpFloor;
    }

    public int getDropOffFloor(){
        return this.dropOffFloor;
    }
    private void setDropOffFloor(int dropOffFloor){
        this.dropOffFloor=dropOffFloor;
    }

    public int getPassengerCount(){
        return this.passengerCount;
    }
    private void setPassengerCount(int passengerCount){
        this.passengerCount=passengerCount;
    }

    public LiftRequestStatus getStatus(){
        return this.status;
    }
    public void setStatus(LiftRequestStatus status){
        this.status=status;
        try (Connection connection = DriverManager.getConnection(DBConstants.URL, DBConstants.USER, DBConstants.PASSWORD)){
            LiftRequestsTableUtility.updateStatusByRequestId(connection, this.getId(), this.status);
        }catch (Exception e){
            System.out.println("Exception occurred: "+e.getMessage());
        }
    }

    public LiftRequest(Connection connection, int liftId, int pickUpFloor,
                       int dropOffFloor, int passengerCount) throws SQLException {
        setId(LiftRequestsTableUtility.getNoOfExistingRequests(connection)+1);
        setLiftId(liftId);
        setPickUpFloor(pickUpFloor);
        setDropOffFloor(dropOffFloor);
        setPassengerCount(passengerCount);
        setStatus(LiftRequestStatus.pending);
        LiftRequestsTableUtility.addNewLiftRequest(connection, this);
    }
}
