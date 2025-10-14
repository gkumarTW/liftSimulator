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
        CLIUtility.coreCLI();
    }
}

