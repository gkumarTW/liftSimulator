package utility;

import exception.InvalidInputException;
import lifts.brands.NormalLift;

import java.util.List;
import java.util.Scanner;

public class LiftSimulatorUtility {

    public boolean onlyDigits(String s) {

        // Traverse each character in the string
        for (int i = 0; i < s.length(); i++) {

            // Check if the character is not a digit
            if (!Character.isDigit(s.charAt(i))) {

                // If any character is not a digit, return false
                return false;
            }
        }
        return true;  // If all characters are digits, return true
    }

    public boolean checkInput(String mainStr, String... inputs) {
        for (String input : inputs) {
            if (input.equalsIgnoreCase(mainStr)) {
                return true;
            }
        }
        return false;
    }

    public void inputLifts(Scanner sc, List<NormalLift> lifts, int totalNumberOfLifts, int maxFloor) {
        while (lifts.size() != totalNumberOfLifts) {
            int currentLiftId = lifts.size() + 1;
            System.out.println("Configure lift " + currentLiftId);
            int currentLiftMaxFloor, currentLiftMinFloor, currentLiftMaxCapacity;
            while (true) {
                try {
                    System.out.println("Max floors for this lift:");
                    int input = sc.nextInt();
                    if (input > maxFloor || input <= 0)
                        throw new InvalidInputException();
                    currentLiftMaxFloor = input;
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            while (true) {
                try {
                    System.out.println("Max passengers for this lift:");
                    int input = sc.nextInt();
                    if (input <= 0)
                        throw new InvalidInputException();
                    currentLiftMaxCapacity = input;
                    break;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            currentLiftMinFloor = 0;

            NormalLift currentLift = new NormalLift(lifts.size() + 1,
                    currentLiftMinFloor, currentLiftMaxFloor, currentLiftMaxCapacity);

            lifts.add(currentLift);

            System.out.println("Created lift with id: " + currentLift.liftId);

        }

    }

}
