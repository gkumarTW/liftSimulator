class LiftFullException extends Exception {
    public LiftFullException(String message) {
        super(message);
    }

    public LiftFullException(){
        super("Lift is full");
    }
}
