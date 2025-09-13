import java.util.*;

public class LiftSimulator{

    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);

        //LiftManager is used to manage building, lifts and handle user lift requests
        LiftManager liftManager=new LiftManager();

        //Taking input configuration from user
        liftManager.inputBuildingConfiguration(sc);

        //Taking lift configuration from user
        liftManager.inputLifts(sc);

        //Running lifts on separate threads
        liftManager.startLifts();

        //UI for processing lift requests start here
        System.out.println();
        System.out.println("TW lift park simulation started.");
        System.out.println("You can request a lift in this building or type EXIT to stop.");
        sc.nextLine();

        while(true){
            //INPUT CURRENT FLOOR, DESTINATION FLOOR, AND PASSENGER COUNT
            System.out.println();
            System.out.println("Type 'EXIT' to quit or 'showLifts' to view lifts.");
            System.out.println("This building has floors "+
                    (liftManager.getMinFloor()==0?"G":liftManager.getMinFloor())+" to "+liftManager.getMaxFloor());
            System.out.println("Enter current floor, destination floor, passengers (e.g. 2 5 3):");
            String combinedInputStr=sc.nextLine();

            //Check if input has any keywords
            if(combinedInputStr.equalsIgnoreCase("exit")){
                liftManager.stopLifts();
                break;
            }else if(combinedInputStr.equalsIgnoreCase("showLifts")){
                liftManager.showLifts();
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
                //Taking 'G' as ground-floor input and then storing it as zero
                if(combinedInputArr[0].equalsIgnoreCase("g"))
                    currentFloor=0;
                else
                    currentFloor=Integer.parseInt(combinedInputArr[0]);

                int destinationFloor=Integer.parseInt(combinedInputArr[1]);
                int passengerCount=Integer.parseInt(combinedInputArr[2]);

                //Check if a single lift can accommodate requested passenger count
                if(passengerCount> liftManager.getMaxCapacityOfLifts() || passengerCount<=0){
                    throw new InvalidInputException("A single lift cannot fit the requested no of passengers.");
                }

                //Check if the request from and to floors are not equal
                if(currentFloor==destinationFloor)
                    throw new InvalidInputException("Pick up and drop off floors have to be different.");

                //Check if the request from and to floors are within lift serviceable floors
                if (currentFloor>liftManager.getServiceFloors()||destinationFloor>liftManager.getServiceFloors())
                    throw new InvalidInputException("Lift cannot service to requested floors.");


                int assignedLiftId=liftManager
                        .handleLiftRequest(new LiftRequest(currentFloor, destinationFloor, passengerCount));
                System.out.println("Lift no "+ assignedLiftId +" has been assigned to you.");
                //USE THE BELOW BREAK TO EXECUTE ONLY ONE USER INPUT CASE
//                break;
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

