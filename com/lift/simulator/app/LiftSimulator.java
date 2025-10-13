package com.lift.simulator.app;

import com.lift.simulator.exceptions.InvalidInputException;
import com.lift.simulator.utility.CLIUtility;
import com.lift.simulator.process.LiftManager;
import com.lift.simulator.constants.DBConstants;
import com.lift.simulator.utility.DBUtility;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Scanner;

public class LiftSimulator {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(DBConstants.URL,
                DBConstants.USER, DBConstants.PASSWORD)) {

            DBUtility.prepareDB(connection);

            //com.lift.simulator.process.LiftManager is used to manage building, lifts and handle user lift requests
            LiftManager liftManager = new LiftManager();

            //Taking input configuration from user
            liftManager.inputBuildingConfiguration(sc, connection);

            //Taking lift configuration from user
            liftManager.inputLifts(sc, connection);

            //Running lifts on separate threads
            liftManager.startLifts();

            //UI for processing lift requests start here
            System.out.println();
            System.out.println("TW lift park simulation started.");
            System.out.println("You can request a lift in this building or type EXIT to stop.");
            sc.nextLine();

            outter:while (true) {
                try {
                    System.out.println("Choose type of requests:");
                    System.out.println("a. batch requests");
                    System.out.println("b. individual requests");
                    System.out.println("c. exit");
                    String option = sc.nextLine();

                    switch (option.charAt(0)) {
                        case 'a':
                            CLIUtility.batchProcessing(sc, liftManager);
                            break;
                        case 'b':
                            CLIUtility.singleRequestProcessing(sc, liftManager);
                            break;
                        case 'c':
                            break outter;
                        default:
                            throw new InvalidInputException("Invalid option input for type of requests");
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        } catch (Exception e) {
            if (e.getMessage() != null)
                System.out.println(e.getMessage());
            else
                System.out.println("EXCEPTION OCCURRED!");
        }
    }
}

