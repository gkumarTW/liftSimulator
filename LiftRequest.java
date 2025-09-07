class LiftRequest {
    final int fromFloor;
    final int toFloor;
    final int passengerCount;

    LiftRequest(int fromFloor, int toFloor, int passengerCount) {
        this.passengerCount = passengerCount;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }
}