import java.util.*;

public class LiftSimulator{
    public static boolean onlyDigits(String s) {

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

    public static boolean checkInput(String mainStr, String... inputs){
        for(String input: inputs){
            if(input.equalsIgnoreCase(mainStr)){
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args){
        Scanner sc =new Scanner(System.in);

        //hard coding the total number of floors for now
        int maxFloors=8;

        int minFloors=0;

        int liftCapacity=4;

        //Creating 4 lifts with liftId's and total floors
        Lift lift1=new Lift(1, minFloors, maxFloors, liftCapacity);
        Lift lift2=new Lift(2, minFloors, maxFloors, liftCapacity);
        Lift lift3=new Lift(3, minFloors, maxFloors, liftCapacity);
        Lift lift4=new Lift(4, minFloors, maxFloors, liftCapacity);


        //Created an instance for lift manager to handle User requests
        LiftManager liftManager=new LiftManager(maxFloors,lift1,lift2,lift3,lift4);

        liftManager.startLifts();

        System.out.println("Welcome to TW lift park!");
        System.out.println("You can request a lift in this building.");
        System.out.println("Please press ENTER to continue");

        sc.nextLine();


        while(true){

            //INPUT CURRENT FLOOR, DESTINATION FLOOR, AND PASSENGER COUNT

            System.out.println();
            System.out.println("Type EXIT anytime to stop lift simulation.");
            System.out.println("This building has floors "+minFloors+" to "+maxFloors);
            System.out.println("Enter current floor followed by destination floor followed by no of people boarding(x y z):");
            String combinedInputStr=sc.nextLine();

            if(combinedInputStr.equalsIgnoreCase("exit")){
                liftManager.stopLifts();
                break;
            }else if(combinedInputStr.equalsIgnoreCase("showLifts")){
                System.out.println(lift1);
                System.out.println(lift2);
                System.out.println(lift3);
                System.out.println(lift4);
                continue;
            }

            String[] combinedInputArr=combinedInputStr.split(" ");

            //Check if the input has three values
            if(combinedInputArr.length<3){
                System.out.println("Enter valid input");
                continue;
            }

            try{
                int currentFloor;

                //Taking G as ground-floor input and then storing it as zero
                if(combinedInputArr[0].equalsIgnoreCase("g"))
                    currentFloor=0;
                else
                    currentFloor=Integer.parseInt(combinedInputArr[0]);
                int destinationFloor=Integer.parseInt(combinedInputArr[1]);
                int passengerCount=Integer.parseInt(combinedInputArr[2]);
                int assignedLiftId=liftManager.handleLiftRequest(new LiftRequest(currentFloor, destinationFloor, passengerCount));
                System.out.println("Lift no "+ assignedLiftId +" has been assigned to you.");
//                break;

            }catch (NumberFormatException e){
                System.out.println("Enter valid input");
            }
            catch (Exception e){
                if(e.getMessage()!=null)
                    System.out.println(e.getMessage());
                else
                    System.out.println("EXCEPTION OCCURRED!");
            }

        }
    }
}

