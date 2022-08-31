package com.flab.yousinsa.product.domain.events;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class SellProductEvent {
	private Long soldProductOptionId;
	private Integer purchaseAmount;
	private Long purchaseOrderId;
	private Long remainedStock;
	private LocalDateTime eventPublishedTime;
}
