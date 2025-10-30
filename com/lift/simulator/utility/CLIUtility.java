package com.lift.simulator.utility;

import com.lift.simulator.exceptions.InvalidInputException;
import com.lift.simulator.exceptions.LiftFullException;
import com.lift.simulator.process.LiftManager;
import com.lift.simulator.process.TemporaryLiftRequest;
import com.lift.simulator.dto.LiftRequestDTO;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class CLIUtility {

    // restricting object creation this class
    private CLIUtility(){}

    // public interface methods
    public static void batchProcessing(Scanner sc, LiftManager liftManager)
            throws IOException {
        System.out.println("Enter a batch number to process " +
                "requests(below batch numbers are available):");
        Map<Integer, List<LiftRequestDTO>> requests = ResourceUtility.loadLiftRequests();

        while (true) {
            try {
                displayAllOptions(requests.keySet());

                int selectedBatchId = sc.nextInt();
                sc.nextLine();

                if (!requests.containsKey(selectedBatchId)) {
                    throw new InvalidInputException("Please select an existing batchId.");
                }

                processBatchLiftRequests(requests, selectedBatchId, liftManager);
                break;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void singleRequestProcessing(Scanner sc, LiftManager liftManager)
            throws SQLException, LiftFullException {
        boolean exitTriggered = false;
        while (!exitTriggered) {
            //Input CURRENT FLOOR, DESTINATION FLOOR, AND PASSENGER COUNT as a string
            String combinedInputStr = inputLiftRequestStr(sc, liftManager);

            //Check if input has any keywords
            if (combinedInputStr.equalsIgnoreCase("exit")) {
                liftManager.stopLifts();
                exitTriggered=true;
            } else if (combinedInputStr.equalsIgnoreCase("showLifts")) {
                liftManager.showLifts();
                continue;
            }
            processSingleLiftRequestStr(combinedInputStr, liftManager);
        }
    }

    public static void coreCLI(){
        Scanner sc = new Scanner(System.in);

        try {

            DBUtility.prepareDB();

            //com.lift.simulator.process.LiftManager is used to manage building, lifts and handle user lift requests
            LiftManager liftManager = new LiftManager(sc);

            //UI for processing lift requests start here
            System.out.println();
            System.out.println("TW lift park simulation started.");
            System.out.println("You can request a lift in this building or type EXIT to stop.");
//            sc.nextLine();

            while (true) {
                TreeMap<Character, String> typeOfRequestsOptions = new TreeMap<>();
                typeOfRequestsOptions.put('a', "batch requests");
                typeOfRequestsOptions.put('b', "individual requests");
                typeOfRequestsOptions.put('c', "exit");

                char option = inputTypeOfRequest(sc, typeOfRequestsOptions);

                boolean exitTriggered = false;

                switch (option){
                    case 'a':
                        batchProcessing(sc, liftManager);
                        break ;
                    case 'b':
                        singleRequestProcessing(sc, liftManager);
                        break ;
                    case 'c':
                        exitTriggered = true;
                        break;
                    default:
                        System.out.println("Invalid input");
                }

                if(exitTriggered){
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Hii");
            if (e.getMessage() != null)
                System.out.println(e.getMessage());
            else
                System.out.println("EXCEPTION OCCURRED!");
        }
    }

    // private helper methods

    private static char inputTypeOfRequest(Scanner sc, TreeMap<Character, String> options)
            throws InvalidInputException{
        while(true){
            try{
                System.out.println("Choose type of requests:");

                for (Map.Entry<Character, String> entry : options.entrySet()) {
                    System.out.println(entry.getKey() + ". " + entry.getValue());
                }


                String input = sc.nextLine();

                if(input.isEmpty()){
                    throw new InvalidInputException("No input provided");
                }
                char option = input.toLowerCase().charAt(0);

                if(options.containsKey(option)){
                    return option;
                }
                throw new InvalidInputException("Invalid option input for type of requests");
            } catch (Exception e) {
                System.out.println("Invalid input: "+e.getMessage());
            }
        }

    }


    private static void displayAllOptions(Set<Integer> options){
        options.forEach(x -> System.out.println("batch no: " + x));
    }

    private static void processBatchLiftRequests(Map<Integer, List<LiftRequestDTO>> requests,
                                                 int batchId, LiftManager liftManager) {
        for (LiftRequestDTO request : requests.get(batchId)) {
            try {
                int assignedLiftId = liftManager.handleLiftRequest(
                        new TemporaryLiftRequest(
                                request.pickUpFloor(),
                                request.dropOffFloor(),
                                request.passengerCount()
                        )
                );
                System.out.println("Lift no " + assignedLiftId + " has been assigned to request " + request);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static String inputLiftRequestStr(Scanner sc, LiftManager liftManager){
        System.out.println();
        System.out.println("Type 'EXIT' to quit or 'showLifts' to view lifts.");
        System.out.println("This building has floors " +
                (liftManager.getMinFloor() == 0 ? "G" : liftManager.getMinFloor()) +
                " to " + liftManager.getMaxFloor());
        System.out.println("Enter current floor, destination floor, passengers (e.g. 2 5 3):");
        return sc.nextLine();
    }

    private static void processSingleLiftRequestArr(String[] combinedInputArr, LiftManager liftManager)
            throws SQLException, LiftFullException {
        //Check if the input has three values
        if (combinedInputArr.length < 3) {
            throw new InvalidInputException("Invalid input");
        }

        int currentFloor;
        //Taking 'G' as ground-floor input and then storing it as zero
        if (combinedInputArr[0].equalsIgnoreCase("g"))
            currentFloor = 0;
        else
            currentFloor = Integer.parseInt(combinedInputArr[0]);

        int destinationFloor;
        //Taking 'G' as ground-floor input and then storing it as zero
        if (combinedInputArr[1].equalsIgnoreCase("g"))
            destinationFloor = 0;
        else
            destinationFloor = Integer.parseInt(combinedInputArr[1]);

        int passengerCount = Integer.parseInt(combinedInputArr[2]);

        //Check if a single lift can accommodate requested passenger count
        if (passengerCount > liftManager.getMaxCapacityOfLifts() || passengerCount <= 0) {
            throw new InvalidInputException("A single lift cannot fit " +
                    "the requested no of passengers.");
        }

        //Check if the request from and to floors are not equal
        if (currentFloor == destinationFloor)
            throw new InvalidInputException("Pick up and drop off floors have to be different.");

        //Check if the request from and to floors are within lift serviceable floors
        if (currentFloor > liftManager.getServiceFloors() ||
                destinationFloor > liftManager.getServiceFloors())
            throw new InvalidInputException("Lift cannot service to requested floors.");


        int assignedLiftId = liftManager
                .handleLiftRequest(new TemporaryLiftRequest(currentFloor,
                        destinationFloor, passengerCount));
        System.out.println("Lift no " + assignedLiftId + " has been assigned to you.");

    }

    private static void processSingleLiftRequestStr(String combinedInputStr, LiftManager liftManager)
            throws SQLException, LiftFullException, InvalidInputException {
        String[] combinedInputArr = combinedInputStr.split(" ");

        processSingleLiftRequestArr(combinedInputArr, liftManager);
    }
}
