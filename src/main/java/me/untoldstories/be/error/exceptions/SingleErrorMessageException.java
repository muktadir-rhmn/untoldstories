package me.untoldstories.be.error.exceptions;

public class SingleErrorMessageException extends RuntimeException{
    private final String message;

    public SingleErrorMessageException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
