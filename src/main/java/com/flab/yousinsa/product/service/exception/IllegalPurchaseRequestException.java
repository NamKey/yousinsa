package com.flab.yousinsa.product.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class IllegalPurchaseRequestException extends RuntimeException {
	public IllegalPurchaseRequestException(String message) {
		super(message);
	}

	public IllegalPurchaseRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public IllegalPurchaseRequestException(Throwable cause) {
		super(cause);
	}
}
