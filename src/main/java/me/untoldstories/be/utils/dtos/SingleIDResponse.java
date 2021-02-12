package me.untoldstories.be.utils.dtos;

public class SingleIDResponse {
	private final Long id;

	public SingleIDResponse(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
