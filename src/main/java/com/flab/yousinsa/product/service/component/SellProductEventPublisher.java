package com.flab.yousinsa.product.service.component;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.flab.yousinsa.product.domain.events.SellProductEvent;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SellProductEventPublisher {

	private final ApplicationEventPublisher eventPublisher;

	public void publishSellProductEvent(SellProductEvent sellProductEvent) {
		eventPublisher.publishEvent(sellProductEvent);
	}
}
