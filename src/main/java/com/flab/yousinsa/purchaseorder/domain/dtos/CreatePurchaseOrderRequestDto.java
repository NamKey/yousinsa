package com.flab.yousinsa.purchaseorder.domain.dtos;

import javax.validation.constraints.Min;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePurchaseOrderRequestDto {
	private Long productOptionId;

	@Min(value = 1)
	private Integer purchaseOrderAmount;
}
