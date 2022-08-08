package com.flab.yousinsa.global.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalResourceConditionException extends RuntimeException {
	public IllegalResourceConditionException(String message) {
		super(message);
	}

	public IllegalResourceConditionException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalResourceConditionException(Throwable cause) {
		super(cause);
	}
}
