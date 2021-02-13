package me.untoldstories.be.error.exceptions;

public class InternalServerErrorException extends RuntimeException {
	public static InternalServerErrorException EMPTY_EXCEPTION = new InternalServerErrorException();

	private InternalServerErrorException() { }
	public InternalServerErrorException(Throwable throwable) {
		super(throwable);
	}
}
