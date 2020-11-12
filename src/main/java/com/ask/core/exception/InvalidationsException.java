package com.ask.core.exception;

import java.util.ArrayList;
import java.util.List;

public class InvalidationsException extends BasicException {
	private static final long serialVersionUID = -8714368702153951956L;

	private List<ResponseMessage> exceptionMessages = new ArrayList<ResponseMessage>();

	public List<ResponseMessage> getExceptionMessages() {
		return exceptionMessages;
	}

	public void setExceptionMessages(List<ResponseMessage> exceptionMessages) {
		this.exceptionMessages = exceptionMessages;
	}

	public InvalidationsException add(String message) {
		exceptionMessages.add(ResponseMessage.invalid(message));
		return this;
	}

	public InvalidationsException add(String field, String message) {
		exceptionMessages.add(ResponseMessage.invalid(field, message));
		return this;
	}

	public InvalidationsException() {
		super();
	}

	public InvalidationsException(String message) {
		super(message);
		exceptionMessages.add(ResponseMessage.invalid(message));
	}

	public InvalidationsException(String field, String message) {
		super(message);
		exceptionMessages.add(ResponseMessage.invalid(field, message));
	}

	public int size() {
		return this.exceptionMessages.size();
	}

	public boolean isNotEmpty() {
		return !this.exceptionMessages.isEmpty();
	}
}
