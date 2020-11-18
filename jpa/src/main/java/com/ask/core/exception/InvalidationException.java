package com.ask.core.exception;

public class InvalidationException extends BasicException {
	private static final long serialVersionUID = -885675546754024737L;

	protected ResponseMessage exceptionMessage;

	public InvalidationException() {
		super();
	}

	public InvalidationException(String message) {
		super(message);
		this.exceptionMessage = ResponseMessage.invalid(message);
	}

	public InvalidationException(String field, String message) {
		super(message);
		this.exceptionMessage = ResponseMessage.invalid(field, message);
	}

	public ResponseMessage getExceptionMessage() {
		return exceptionMessage;
	}
	public void setExceptionMessage(ResponseMessage exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
}
