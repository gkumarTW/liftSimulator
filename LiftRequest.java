public class LiftRequest {
    private final int fromFloor;   // starting floor
    private final int toFloor;     // destination floor

    public int getFromFloor() {
        return this.fromFloor;
    }

    public int getToFloor() {
        return this.toFloor;
    }

    public LiftRequest(int fromFloor, int toFloor) {
        this.fromFloor = fromFloor;
        this.toFloor = toFloor;
    }
}
