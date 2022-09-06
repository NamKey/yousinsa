package com.flab.yousinsa.store.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class IllegalStoreAccessException extends RuntimeException {
	public IllegalStoreAccessException(String message) {
		super(message);
	}

	public IllegalStoreAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalStoreAccessException(Throwable cause) {
		super(cause);
	}
}
