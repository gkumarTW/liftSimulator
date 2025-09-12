class LiftFullException extends Exception {
    public LiftFullException(String message) {
        super(message);
    }

    public LiftFullException(){
        super("Lift is full please try after some time");
    }
}
