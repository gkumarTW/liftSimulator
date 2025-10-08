package exception;

public class InvalidFloorException extends RuntimeException {
    public InvalidFloorException() {
        super("Invalid floor");
    }

    public InvalidFloorException(String message) {
        super(message);
    }
}
