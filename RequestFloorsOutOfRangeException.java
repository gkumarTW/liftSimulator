public class RequestFloorsOutOfRangeException extends RuntimeException {
    public RequestFloorsOutOfRangeException(String message) {
        super(message);
    }

    public RequestFloorsOutOfRangeException() {
        super("Request is out of range");
    }
}
