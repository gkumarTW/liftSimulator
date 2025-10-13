package com.lift.simulator.utility;

import com.lift.simulator.exceptions.InvalidInputException;
import com.lift.simulator.exceptions.LiftFullException;
import com.lift.simulator.process.LiftManager;
import com.lift.simulator.process.TemporaryLiftRequest;
import com.lift.simulator.dto.LiftRequestDTO;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CLIUtility {
    public static void batchProcessing(Scanner sc, LiftManager liftManager) throws IOException {
        System.out.println("Enter a batch number to process " +
                "requests(below batch numbers are available):");
        Map<Integer, List<LiftRequestDTO>> requests = ResourceUtility.loadLiftRequests
                ("C:\\Users\\ngaddamanugu\\IdeaProjects\\liftSimulator\\com" +
                        "\\lift\\simulator\\resources\\liftRequests.json");

        while (true) {
            try {
                requests.keySet()
                        .forEach(x -> System.out.println("batch no: " + x));
                int selectedBatchId = sc.nextInt();
                sc.nextLine();
                if (requests.containsKey(selectedBatchId)) {
                    for (LiftRequestDTO request : requests.get(selectedBatchId)) {
                        try{
                            int assignedLiftId = liftManager.handleLiftRequest(new TemporaryLiftRequest(
                                    request.getPickUpFloor(), request.getDropOffFloor(), request.getPassengerCount()));
                            System.out.println("Lift no " + assignedLiftId + " has been assigned to " +
                                    "request " + request);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                } else {
                    throw new InvalidInputException("Please select an existing batchId.");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void singleRequestProcessing(Scanner sc, LiftManager liftManager) throws SQLException, LiftFullException {
        while (true) {
            //INPUT CURRENT FLOOR, DESTINATION FLOOR, AND PASSENGER COUNT
            System.out.println();
            System.out.println("Type 'EXIT' to quit or 'showLifts' to view lifts.");
            System.out.println("This building has floors " +
                    (liftManager.getMinFloor() == 0 ? "G" : liftManager.getMinFloor()) +
                    " to " + liftManager.getMaxFloor());
            System.out.println("Enter current floor, destination floor, passengers (e.g. 2 5 3):");
            String combinedInputStr = sc.nextLine();

            //Check if input has any keywords
            if (combinedInputStr.equalsIgnoreCase("exit")) {
                liftManager.stopLifts();
                break;
            } else if (combinedInputStr.equalsIgnoreCase("showLifts")) {
                liftManager.showLifts();
                continue;
            }
            String[] combinedInputArr = combinedInputStr.split(" ");

            //Check if the input has three values
            if (combinedInputArr.length < 3) {
                System.out.println("Enter valid input");
                continue;
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
            //USE THE BELOW BREAK TO EXECUTE ONLY ONE USER INPUT CASE
//                            break;

        }

    }
}
