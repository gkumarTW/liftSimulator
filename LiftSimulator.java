import java.util.*;

public class LiftSimulator{
    public static void main(String[] args){
        Scanner sc =new Scanner(System.in);

        //hard coding the total number of floors for now
        int maxFloors=10;

        //Creating 4 lifts with liftId's and total floors
        Lift lift1=new Lift(1,maxFloors);
        Lift lift2=new Lift(2,maxFloors);
        Lift lift3=new Lift(3,maxFloors);
        Lift lift4=new Lift(4,maxFloors);

        //Created an instance for lift manager to handle User requests
        LiftManager liftManager=new LiftManager(maxFloors,lift1,lift2,lift3,lift4);

        liftManager.startLifts();

        System.out.println("Welcome to TW lift park!");
        System.out.println("You can request a lift in this building.");
        System.out.println("Please press ENTER to continue");

        sc.nextLine();

        while(true){
            System.out.println();
            System.out.println("Type EXIT anytime to stop lift simulation.");
            System.out.println("This building has floors 1 to "+maxFloors);
            System.out.println("Enter your current floor followed by destination floor:");
            String strCurrDes=sc.nextLine();
            String[] arrCurrAndDes=strCurrDes.split(" ");
            if(strCurrDes.equalsIgnoreCase("exit")){
                liftManager.stopLifts();
                break;
            }
            else if(strCurrDes.equalsIgnoreCase("showLifts")){
                System.out.println(lift1);
                System.out.println(lift2);
                System.out.println(lift3);
                System.out.println(lift4);
            }

            else if(arrCurrAndDes.length<2)
                System.out.println("Please enter a valid input.");

            else{
                try{
                    int currFloor=Integer.parseInt(arrCurrAndDes[0]);
                    int desFloor=Integer.parseInt(arrCurrAndDes[1]);
                    int assignedLiftId=liftManager.handleLiftRequest(new LiftRequest(currFloor, desFloor));
                    System.out.println("Lift no "+ assignedLiftId +" has been assigned to you.");
//                    break;

                }catch(InvalidFloorException e){
                    System.out.println(e.getMessage());
                }catch (Exception e){
                    if(e.getMessage()!=null)
                        System.out.println(e.getMessage());
                    else
                        System.out.println("EXCEPTION OCCURRED!");
                }
            }
        }
    }
}

