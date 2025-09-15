public class InvalidFloorException extends RuntimeException {
    public InvalidFloorException() {
        super("Invalid floor");
    }

    public InvalidFloorException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        return "Invalid input! Please enter numbers within the building range.";
    }
}
