package com.flab.yousinsa.product.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NO_CONTENT)
public class OutOfStockException extends RuntimeException {
	public OutOfStockException(String message) {
		super(message);
	}

	public OutOfStockException(String message, Throwable cause) {
		super(message, cause);
	}

	public OutOfStockException(Throwable cause) {
		super(cause);
	}
}
