package me.untoldstories.be.utils.dtos;

public class SingleMessageResponse {
	public final static SingleMessageResponse OK = new SingleMessageResponse("ok"); //for reusing the same object

	private final String msg;
	public SingleMessageResponse(String message) {
		this.msg = message;
	}

	public String getMsg() {
		return msg;
	}
}
