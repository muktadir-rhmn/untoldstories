package me.untoldstories.be.error.exceptions;

public class AssertionFailedException extends InternalServerErrorException {
    public static final AssertionFailedException EMPTY = new AssertionFailedException();
}
