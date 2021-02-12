package me.untoldstories.be.utils.dtos;

public class SingleMessageResponse {
	public final static SingleMessageResponse SUCCESS_RESPONSE = new SingleMessageResponse("success"); //for reusing the same object

	private final String message;
	public SingleMessageResponse(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
