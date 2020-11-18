package com.ask.core.exception;

import java.io.Serializable;

import org.springframework.http.HttpStatus;


public class ResponseMessage implements Serializable {
	private static final long serialVersionUID = 8535249061907775899L;
	private String field;
	private String message;
	private Integer status;

	public ResponseMessage() {

	}

	public ResponseMessage(HttpStatus status, String field, String message) {
		this.status = status.value();
		this.field = field;
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}

    public static ResponseMessage success(String message) {
        return new ResponseMessage(HttpStatus.OK, null, message);
    }
    public static ResponseMessage invalid(String message) {
        return new ResponseMessage(HttpStatus.BAD_REQUEST, null, message);
    }
    public static ResponseMessage invalid(String field, String message) {
        return new ResponseMessage(HttpStatus.BAD_REQUEST, field, message);
    }
    public static ResponseMessage notFound(String message) {
        return new ResponseMessage(HttpStatus.NOT_FOUND, null, message);
    }
    public static ResponseMessage notFound(String field, String message) {
    	return new ResponseMessage(HttpStatus.NOT_FOUND, field, message);
    }
    public static ResponseMessage conflict(String message) {
    	return new ResponseMessage(HttpStatus.CONFLICT, null, message);
    }
    public static ResponseMessage conflict(String field, String message) {
    	return new ResponseMessage(HttpStatus.CONFLICT, field, message);
    }
    public static ResponseMessage error(String message) {
        return new ResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, null, message);
    }
    public static ResponseMessage unauthorized(String message) {
        return new ResponseMessage(HttpStatus.UNAUTHORIZED, null, message);
    }
    public static ResponseMessage forbidden(String message) {
        return new ResponseMessage(HttpStatus.FORBIDDEN, null, message);
    }

	@Override
	public String toString() {
		return "ExceptionMessage [field=" + field + ", message=" + message + ", status=" + status + "]";
	}
}
