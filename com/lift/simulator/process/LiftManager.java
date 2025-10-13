package com.lift.simulator.process;

import com.lift.simulator.exceptions.InvalidFloorException;
import com.lift.simulator.exceptions.InvalidInputException;
import com.lift.simulator.exceptions.LiftFullException;
import com.lift.simulator.exceptions.RequestFloorsOutOfRangeException;
import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.tableUtility.BuildingsTableUtility;
import com.lift.simulator.utility.tableUtility.LiftBrandsTableUtility;
import com.lift.simulator.utility.tableUtility.LiftsTableUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

public class LiftManager {
    private int buildingId;
    private int totalLifts;
    private final int minFloor = 0;//In the building
    private int maxFloor;//In the building
    private int serviceFloors;
    private int maxLiftsCapacity;
    private List<LiftI> liftsList = new LinkedList<>();

    //get methods for private variables
    public int getTotalLifts() {
        return this.totalLifts;
    }

    public int getMinFloor() {
        return this.minFloor;
    }

    public int getMaxFloor() {
        return this.maxFloor;
    }

    public int getServiceFloors() {
        return this.serviceFloors;
    }

    public int getMaxCapacityOfLifts() {
        return this.maxLiftsCapacity;
    }

    //Overloading constructors
    public LiftManager() {}

    public LiftManager(int maxFloor, LiftI... lifts) {
        this.maxFloor = maxFloor;
        this.liftsList = new LinkedList<>();
        this.liftsList.addAll(Arrays.asList(lifts));
    }

    public LiftManager(int maxFloor, List<LiftI> lifts) {
        //A LinkedList should hold all the lifts
        this.liftsList = new LinkedList<>(lifts);
    }

    public void inputBuildingConfiguration(Scanner sc, Connection connection) throws SQLException {
        System.out.println("Please configure the building...");

        while (true) {
            System.out.println("Enter total floors:");
            int maxFloorInput = sc.nextInt();
            if (maxFloorInput < 0) {
                System.out.println("Invalid input");
                continue;
            }
            this.maxFloor = maxFloorInput;
            break;
        }
        while (true) {
            System.out.println("Number of lifts: ");
            int totalLiftsCount = sc.nextInt();
            if (totalLiftsCount < 0) {
                System.out.println("Invalid input");
                continue;
            }
            this.totalLifts = totalLiftsCount;
            break;
        }
        BuildingsTableUtility.insertBuildingData(connection, this.minFloor, this.maxFloor, this.totalLifts);

        // Maintaining a static value as current use case will only include a single building;
        buildingId=1;
    }

    /**
     * something about this method
     * @param sc
     * @param connection
     * @throws SQLException
     */
    public void inputLifts(Scanner sc, Connection connection) throws SQLException {
        int maxFloorLiftCanService = 0;
        int maxCapacityOfLifts = 0;

        // Fetch all available brand IDs
        int[] brandIds = LiftBrandsTableUtility.getBrandIds(connection);

        while (liftsList.size() != totalLifts) {
            int selectedBrandId = -1;

            sc.nextLine(); // clear buffer

            while (true) {
                try {
                    System.out.println("Choose a lift brand to continue:");

                    // Display brands dynamically from DB
                    for (int i = 0; i < brandIds.length; i++) {
                        String brandName = LiftBrandsTableUtility.getBrandById(connection, brandIds[i]);
                        System.out.println((char) ('a' + i) + ". " + brandName);
                    }

                    String input = sc.nextLine();
                    if (input.isEmpty()) {
                        throw new InvalidInputException("Input cannot be empty");
                    }

                    char optionInput = Character.toLowerCase(input.charAt(0));
                    int index = optionInput - 'a';

                    if (index < 0 || index >= brandIds.length) {
                        throw new InvalidInputException(
                                "Please choose a valid brand option (a-" + (char) ('a' + brandIds.length - 1) + ")"
                        );
                    }

                    selectedBrandId = brandIds[index];
                    break;

                } catch (InvalidInputException e) {
                    System.out.println(e.getMessage());
                }
            }

            // Building and brand-specific limits
            int buildingMaxFloor = BuildingsTableUtility.getMaxFloorById(connection, 1);
            int brandCapacityLimit = LiftBrandsTableUtility.getTotalCapacityLimitById(connection, selectedBrandId);

            int currentLiftId = liftsList.size() + 1;
            System.out.println("Configure lift " + currentLiftId);

            int currentLiftMaxFloor;
            int currentLiftMaxCapacity;
            int currentLiftMinFloor = 0;

            // Max floors input
            while (true) {
                try {
                    System.out.println("Max floors for this lift:");
                    int input = sc.nextInt();

                    if (input <= 0) {
                        throw new InvalidInputException("Floor count must be greater than 0");
                    }
                    if (input > buildingMaxFloor) {
                        throw new InvalidInputException("Lift cannot serve more than " + buildingMaxFloor + " floors");
                    }

                    currentLiftMaxFloor = input;
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    sc.nextLine(); // clear invalid input
                }
            }

            // Max capacity input
            while (true) {
                try {
                    System.out.println("Max passengers for this lift:");
                    int input = sc.nextInt();

                    if (input <= 0) {
                        throw new InvalidInputException("Passenger capacity must be greater than 0");
                    }
                    if (input > brandCapacityLimit) {
                        throw new InvalidInputException("Lift capacity exceeds brand limit of " + brandCapacityLimit + " passengers");
                    }

                    currentLiftMaxCapacity = input;
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    sc.nextLine(); // clear invalid input
                }
            }

            // Create Lift object
            Lift currentLift = new Lift(
                    connection,
                    currentLiftId,
                    currentLiftMinFloor,
                    currentLiftMaxFloor,
                    currentLiftMaxCapacity,
                    this.buildingId,
                    selectedBrandId,
                    currentLiftMaxCapacity
            );

            liftsList.add(currentLift);

            String selectedBrandName = LiftBrandsTableUtility.getBrandById(connection, selectedBrandId);
            System.out.println("Created " + selectedBrandName + " lift with id: " + currentLift.getLiftId());

            // Track maxes
            maxFloorLiftCanService = Math.max(maxFloorLiftCanService, currentLiftMaxFloor);
            maxCapacityOfLifts = Math.max(maxCapacityOfLifts, currentLiftMaxCapacity);

            // Persist in DB
            LiftsTableUtility.addNewLift(connection, currentLift);
        }

        this.serviceFloors = maxFloorLiftCanService;
        this.maxLiftsCapacity = maxCapacityOfLifts;
    }


    //used to assign the lift for the request made
    public int handleLiftRequest(TemporaryLiftRequest request) throws
            InvalidFloorException, LiftFullException, RequestFloorsOutOfRangeException, SQLException {
        //Checking if the requested fromFloor and toFloor are in building's range
        if (request.getDropOffFloor() < 0 || request.getDropOffFloor() > this.maxFloor
                || request.getPickUpFloor() < 0 || request.getPickUpFloor() > this.maxFloor) {
            throw new InvalidFloorException("Invalid floor in request");
        }

        if(request.getPickUpFloor() > this.getServiceFloors() || request.getDropOffFloor() > this.getServiceFloors()){
            throw new InvalidFloorException("Lift cannot service the floor in request: " + request);
        }

        if(request.getPassengerCount() > this.getMaxCapacityOfLifts()){
            throw new InvalidInputException("Single lift cannot accommodate the requested passengers.");
        }

        /* Find suitable lift (dependencies are request's fromFloor, toFloor, passengerCount
         * and lift's currentCapacity, totalCapacity, state )
         */
        LiftI nearestLift = findNearestLift(request);

        //findNearestLift method will return null if no lift can fit the requested passengerCount
        if (nearestLift == null) {
            throw new LiftFullException();
        }

        nearestLift.addPassengers(request.getPassengerCount());
//        nearestLift.addRequest(request);
        try(Connection connection = DriverManager.getConnection(DBConstants.URL,
                DBConstants.USER, DBConstants.PASSWORD)){
            nearestLift.addRequest(new LiftRequest(connection, nearestLift.getLiftId(), request.getPickUpFloor(),
                    request.getDropOffFloor(), request.getPassengerCount()));
        }
        return nearestLift.getLiftId();
    }

    /* This method will return the nearest lift to the requested fromFloor based on distance(that is going
     * towards the request toFloor or the lift that is idle)
     */
    private LiftI findNearestLift(TemporaryLiftRequest request) {
        LiftI nearestLift = null;
        int minDistance = Integer.MAX_VALUE;

        for (LiftI lift : liftsList) {
            //check if request from and to floor is within this lift's limit
            if (request.getPickUpFloor() < lift.getMinFloor() || request.getDropOffFloor() < lift.getMinFloor()
                    || request.getPickUpFloor() > lift.getMaxFloor() || request.getDropOffFloor() > lift.getMaxFloor())
                continue;


            // check if lift can fit requested passengerCount
            if (!lift.canLiftFit(request.getPassengerCount()))
                continue;

            int distance = Math.abs(lift.getCurrentFloor() - request.getPickUpFloor());

            switch (lift.getCurrState()) {
                case idle:

                    if (distance < minDistance) {
                        minDistance = distance;
                        nearestLift = lift;
                    }
                    break;

                case goingUp:
                    if (request.getPickUpFloor() >= lift.getCurrentFloor()
                            && request.getDropOffFloor() > request.getPickUpFloor()) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestLift = lift;
                        }
                    }
                    break;

                case goingDown:
                    if (request.getPickUpFloor() <= lift.getCurrentFloor()
                            && request.getDropOffFloor() < request.getPickUpFloor()) {
                        if (distance < minDistance) {
                            minDistance = distance;
                            nearestLift = lift;
                        }
                    }
                    break;
            }
        }

        return nearestLift;
    }


    //method to start all the lifts inside the map
    public void startLifts() {
        for (LiftI lift : liftsList) {
            new Thread(lift).start();
        }
    }

    //method to stop all the lifts inside the map
    public void stopLifts() {
        for (LiftI lift : liftsList) {
            lift.stopLift();
        }
    }

    public void showLifts() {
        for (LiftI lift : liftsList) {
            System.out.println(lift);
        }
    }

}