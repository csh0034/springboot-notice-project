package com.ask.core.exception;

public class ConflictException extends InvalidationException {
	private static final long serialVersionUID = -4310464073911951484L;

	public ConflictException() {
		super();
	}

	public ConflictException(String message) {
		super(message);
		this.exceptionMessage = ResponseMessage.conflict(message);
	}

	public ConflictException(String field, String message) {
		super(message);
		this.exceptionMessage = ResponseMessage.conflict(field, message);
	}

}
