package exception;

public class LiftFullException extends Exception {
    public LiftFullException(String message) {
        super(message);
    }

    public LiftFullException() {
        super("lifts.brands.NormalLift is full please try after some time");
    }
}
