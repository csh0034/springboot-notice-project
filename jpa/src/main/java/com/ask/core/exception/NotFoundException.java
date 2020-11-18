package com.ask.core.exception;

public class NotFoundException extends InvalidationException {
	private static final long serialVersionUID = -400577472987162368L;

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
		this.exceptionMessage = ResponseMessage.notFound(message);
	}

	public NotFoundException(String field, String message) {
		super(message);
		this.exceptionMessage = ResponseMessage.notFound(field, message);
	}
}
