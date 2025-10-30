package com.lift.simulator.process;

import com.lift.simulator.exceptions.InvalidFloorException;
import com.lift.simulator.exceptions.InvalidInputException;
import com.lift.simulator.exceptions.LiftFullException;
import com.lift.simulator.exceptions.RequestFloorsOutOfRangeException;
import com.lift.simulator.utility.DBUtility;
import com.lift.simulator.utility.tableUtility.BuildingsTableUtility;
import com.lift.simulator.utility.tableUtility.LiftBrandsTableUtility;
import com.lift.simulator.utility.tableUtility.LiftsTableUtility;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class LiftManager {
    private int buildingId;
    private int totalLifts;
    private final int minFloor;//In the building
    private int maxFloor;//In the building
    private int serviceFloors;
    private int maxLiftsCapacity;
    private List<LiftI> liftsList = new LinkedList<>();

    //getter methods for private variables
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

    /**
     * This constructor will take inputs from the user:
     *      First building is configured
     *      Based on how many lifts the building hold the user has to configure those lifts
     *      And At last the lifts start to run on separate threads.
     * @param sc
     * @throws SQLException
     */
    public LiftManager(Scanner sc) throws SQLException {
        //For now keeping the min floor of the building as 0
        this.minFloor = 0;

        //Taking input configuration from user
        this.inputBuildingConfiguration(sc);

        //Taking lift configuration from user
        this.inputLiftsConfiguration(sc);

        //Running lifts on separate threads
        this.startLifts();
    }

    /**
     * Prompts the user to configure building parameters (floors, lift count),
     * validates input, and persists configuration in the Buildings table.
     * @param sc
     * @throws SQLException
     */
    public void inputBuildingConfiguration(Scanner sc) throws SQLException {
        System.out.println("Please configure the building...");

        boolean validInputProvided = false;
        while (!validInputProvided) {
            System.out.println("Enter total floors:");
            int maxFloorInput = sc.nextInt();
            sc.nextLine();
            if (maxFloorInput < 0) {
                System.out.println("Invalid input");
                continue;
            }
            this.maxFloor = maxFloorInput;
            validInputProvided = true;
        }
        validInputProvided = false;
        while (!validInputProvided) {
            System.out.println("Number of lifts: ");
            int totalLiftsCount = sc.nextInt();
            sc.nextLine();
            if (totalLiftsCount < 0) {
                System.out.println("Invalid input");
                continue;
            }
            this.totalLifts = totalLiftsCount;
            validInputProvided = true;
        }
        BuildingsTableUtility.insertBuildingData(this.minFloor, this.maxFloor, this.totalLifts);

        // Maintaining a static value as current use case will only include a single building
        buildingId=1;
    }



    /**
     * Configures all lifts by:
     * - Fetching available lift brands from the DB
     * - Validating brand, floor, and capacity constraints
     * - Creating and persisting each lift
     * Updates overall service floors and capacity limits.
     * @param sc
     * @throws SQLException
     */
    public void inputLiftsConfiguration(Scanner sc) throws SQLException {
        int maxFloorLiftCanService = 0;
        int maxCapacityOfLifts = 0;

        int[] brandIds = LiftBrandsTableUtility.getBrandIds();

        while (liftsList.size() != totalLifts) {
            int selectedBrandId = selectLiftBrand(sc, brandIds);

            int buildingMaxFloor = BuildingsTableUtility.getMaxFloorById(1);
            int brandCapacityLimit = LiftBrandsTableUtility.getTotalCapacityLimitById(selectedBrandId);

            int currentLiftId = liftsList.size() + 1;
            System.out.println("Configure lift " + currentLiftId);

            int currentLiftMaxFloor = inputLiftMaxFloor(sc, buildingMaxFloor);
            int currentLiftMaxCapacity = inputLiftMaxCapacity(sc, brandCapacityLimit);

            createAndPersistLift(currentLiftId, 0, currentLiftMaxFloor,
                    currentLiftMaxCapacity, this.buildingId, selectedBrandId);

            maxFloorLiftCanService = Math.max(maxFloorLiftCanService, currentLiftMaxFloor);
            maxCapacityOfLifts = Math.max(maxCapacityOfLifts, currentLiftMaxCapacity);
        }

        this.serviceFloors = maxFloorLiftCanService;
        this.maxLiftsCapacity = maxCapacityOfLifts;
    }

    // private helper methods for the above method(inputLiftsConfiguration)

    private int selectLiftBrand(Scanner sc, int[] brandIds) throws SQLException {
        while (true) {
            try {
                System.out.println("Choose a lift brand to continue:");

                for (int i = 0; i < brandIds.length; i++) {
                    String brandName = LiftBrandsTableUtility.getBrandById(brandIds[i]);
                    System.out.println((char) ('a' + i) + ". " + brandName);
                }

                String input = sc.nextLine();
                if (input.isEmpty()) throw new InvalidInputException("Input cannot be empty");

                int index = Character.toLowerCase(input.charAt(0)) - 'a';
                if (index < 0 || index >= brandIds.length) {
                    throw new InvalidInputException("Please choose a valid brand option (a-" +
                            (char) ('a' + brandIds.length - 1) + ")");
                }

                return brandIds[index];
            } catch (InvalidInputException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int inputLiftMaxFloor(Scanner sc, int buildingMaxFloor) {
        while (true) {
            try {
                System.out.println("Max floors for this lift:");
                int input = sc.nextInt();
                sc.nextLine();
                if (input <= 0) throw new InvalidInputException("Floor count must be greater than 0");
                if (input > buildingMaxFloor) throw new InvalidInputException("Lift cannot serve more than " + buildingMaxFloor + " floors");

                return input;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private int inputLiftMaxCapacity(Scanner sc, int brandCapacityLimit) {
        while (true) {
            try {
                System.out.println("Max passengers for this lift:");
                int input = sc.nextInt();
                sc.nextLine();
                if (input <= 0) throw new InvalidInputException("Passenger capacity must be greater than 0");
                if (input > brandCapacityLimit) throw new InvalidInputException("Lift capacity exceeds brand limit of " + brandCapacityLimit + " passengers");

                return input;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void createAndPersistLift(int liftId, int minFloor, int maxFloor,
                                      int maxCapacity, int buildingId, int brandId) throws SQLException {
        Lift lift = new Lift(liftId, minFloor, maxFloor, maxCapacity, buildingId, brandId, maxCapacity);
        liftsList.add(lift);
        LiftsTableUtility.addNewLift(lift);
        String brandName = LiftBrandsTableUtility.getBrandById(brandId);
        System.out.println("Created " + brandName + " lift with id: " + lift.getLiftId());
    }


    /**
     * Handles an incoming lift request by:
     * - Validating requested floors and passenger count
     * - Selecting the nearest available lift
     * - Assigning the request and persisting it to the DB
     * @param request
     * @return
     * @throws InvalidFloorException
     * @throws LiftFullException
     * @throws RequestFloorsOutOfRangeException
     * @throws SQLException
     */
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

        nearestLift.addRequest(new LiftRequest(nearestLift.getLiftId(), request.getPickUpFloor(),
                    request.getDropOffFloor(), request.getPassengerCount()));

        return nearestLift.getLiftId();
    }

    /**
     * Finds the most suitable lift for a request based on:
     * - Distance to pickup floor
     * - Lift direction and availability
     * - Capacity constraints
     * Returns the nearest idle or direction-compatible lift, or null if none fit.
     *
     * @param request
     * @return
     */
    private LiftI findNearestLift(TemporaryLiftRequest request) {
        LiftI nearestLift = null;
        int minDistance = Integer.MAX_VALUE;

        for (LiftI lift : liftsList) {
            if(isLiftWithinFloorLimits(lift, request)
                    && isLiftCapacitySufficient(lift, request)
                    && isLiftDirectionCompatible(lift, request)){

                int distance = Math.abs(lift.getCurrentFloor() - request.getPickUpFloor());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestLift = lift;
                }
            }
        }

        return nearestLift;
    }

    // private helper methods to the above method(findNearestLift)

    private boolean isLiftWithinFloorLimits(LiftI lift, TemporaryLiftRequest request) {
        int pickUp = request.getPickUpFloor();
        int dropOff = request.getDropOffFloor();
        return pickUp >= lift.getMinFloor() && dropOff >= lift.getMinFloor()
                && pickUp <= lift.getMaxFloor() && dropOff <= lift.getMaxFloor();
    }

    private boolean isLiftCapacitySufficient(LiftI lift, TemporaryLiftRequest request) {
        return lift.canLiftFit(request.getPassengerCount());
    }

    private boolean isLiftDirectionCompatible(LiftI lift, TemporaryLiftRequest request) {
        int pickUp = request.getPickUpFloor();
        int dropOff = request.getDropOffFloor();

        switch (lift.getCurrState()) {
            case idle:
                return true;
            case goingUp:
                return pickUp >= lift.getCurrentFloor() && dropOff > pickUp;
            case goingDown:
                return pickUp <= lift.getCurrentFloor() && dropOff < pickUp;
            default:
                return false;
        }
    }



    // Starts each configured lift on a separate thread
    public void startLifts() {
        for (LiftI lift : liftsList) {
            new Thread(lift).start();
        }
    }

    // Stopping all lifts inside the List
    public void stopLifts() {
        for (LiftI lift : liftsList) {
            lift.stopLift();
        }
    }

    // Displays current status of all lifts
    public void showLifts() {
        for (LiftI lift : liftsList) {
            System.out.println(lift);
        }
    }

}