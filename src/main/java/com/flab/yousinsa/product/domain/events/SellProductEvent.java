package com.flab.yousinsa.product.domain.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class SellProductEvent {
	private Long soldProductOptionId;
	private Integer purchaseAmount;
	private Long purchaseOrderId;
	private Integer remainedStock;
	private LocalDateTime eventPublishedTime;
}
