package com.flab.yousinsa.product.service.component;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import com.flab.yousinsa.product.domain.events.SellProductEvent;
import com.flab.yousinsa.product.service.contract.ProductOptionUpdateService;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderUpdateService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class SellProductEventListener {

	private final ProductOptionUpdateService productOptionUpdateService;
	private final PurchaseOrderUpdateService purchaseOrderUpdateService;

	@Async
	@TransactionalEventListener
	public void handleSellProductEvent(SellProductEvent sellProductEvent) {
		log.info(SellProductEventListener.class.getName() + "::" + sellProductEvent + "::" + "event start");

		productOptionUpdateService.deductProductOption(
			sellProductEvent.getSoldProductOptionId(),
			sellProductEvent.getPurchaseAmount()
		);

		purchaseOrderUpdateService.acceptPurchaseOrderStatus(sellProductEvent.getPurchaseOrderId());

		log.info(SellProductEventListener.class.getName() + "::" + sellProductEvent + "::" + "event end");
	}
}
