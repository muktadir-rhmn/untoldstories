package me.untoldstories.be.error.exceptions;

public class InternalServerErrorException extends RuntimeException {
	public InternalServerErrorException(Throwable throwable) {
		super(throwable);
	}
}
