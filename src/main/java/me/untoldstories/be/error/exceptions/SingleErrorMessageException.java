package me.untoldstories.be.error.exceptions;

public class SingleErrorMessageException extends RuntimeException{
    public static final SingleErrorMessageException DOES_NOT_EXIST = new SingleErrorMessageException("does not exist");

    private final String message;

    public SingleErrorMessageException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
