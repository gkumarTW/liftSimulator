class LiftRequest {
    final int fromFloor;
    final int toFloor;
    final int passengerCount;

    LiftRequest(int fromFloor, int toFloor, int passengerCount) {
        this.passengerCount = passengerCount;
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }

    @Override
    public String toString(){
        return "fromFloor: "+this.fromFloor+" toFloor: "+this.toFloor+" passengers: "+this.passengerCount;
    }
}