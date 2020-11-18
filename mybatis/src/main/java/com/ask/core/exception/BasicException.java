package com.ask.core.exception;

import java.util.Collection;
import java.util.stream.Collectors;

public class BasicException extends RuntimeException {

	private static final long serialVersionUID = 7728265539000596627L;

	public BasicException() {
        super();
    }

	public BasicException(Exception e) {
		super(e);
	}

	public BasicException(String msg) {
		super(msg);
	}

	public BasicException(Throwable ex) {
		super(ex);
	}

	public BasicException(String message, Throwable e) {
		super(message, e);
	}

	public BasicException(Collection<Exception> ex) {
		super(ex.stream().map(e -> e.getClass().getName() + " " + e.getMessage()).collect(Collectors.joining("\n")));
	}
}
