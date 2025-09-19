Lift Simulator – Operation Flow

This document explains how the lift processes requests in the system, from when a request is added to the lift completing pickups and drop-offs.

1. Adding a Request

  Method: addRequest(utility.LiftRequest newRequest)
  
  Flow:
    Validates the floors of the request.
    Adds it to the requestQueue.
    Calls handleRequest() to update pickup and pending drop-off structures.

2. Handling a Request

  Method: handleRequest(utility.LiftRequest request)
  
  Flow:
    Updates pickUpRequests for the pickup floor.
    Creates a utility.DropOffRequest in pendingDropOffRequests (associated with the pickup floor).
    At this stage, the request is queued for lift processing.

3. Lift Thread Loop

Method: run() — the main lift thread executes continuously while isLiftRunning = true.

  a) Handle Pickups
  
    Condition: !pickUpRequests.isEmpty() && liftState == idle
    
    Steps:
      Determine nearest pickup floor: findNearestFloor(pickUpRequests).
      Call pickUpPassenger(nearestFloor):
      Moves the lift via moveUp() / moveDown().
      At each floor: checkPickUps() and checkDropOffs().
      When arrived: boards passengers, activates corresponding drop-offs in activeDropOffRequests.
      
  b) Handle Drop-Offs
  
    Condition: pickUpRequests.isEmpty() && !activeDropOffRequests.isEmpty() && liftState == idle
    
    Steps:
      Call processPendingDropOffs():
      Finds nearest drop-off floor via findNearestFloor(activeDropOffRequests).
      Moves lift using processDropOff(nearestFloor).
      Drops passengers and updates capacity.
    
  c) Idle State
  
    Condition: pickUpRequests.isEmpty() && activeDropOffRequests.isEmpty() && requestQueue.isEmpty()
    
    Action: sets liftState = idle until new requests arrive.
  
  d) Thread Delay
  
    Method: makeLiftThreadWait(200)
    
    Purpose: prevents busy waiting and allows new requests to be added.

4. Simulation Timing

    moveUp() / moveDown() → waits floorTravelTimeMs per floor.
    
    Boarding / drop-off → waits boardingTimeMs.

5. Stopping the Lift

    Method: shutdownLift()
    
    Sets isLiftRunning = false, exiting the thread loop and stopping lift operation.

6. Control Flow Summary
  addRequest()
     → handleRequest()
        → pickUpRequests / pendingDropOffRequests updated
     → run() thread loop:
          ├─ If pickups exist → pickUpPassenger()
          │     └─ moves lift, boards passengers, activates drop-offs
          ├─ Else if only drop-offs exist → processPendingDropOffs()
          │     └─ moves lift, drops passengers
          └─ Else → idle (waiting for next request)
