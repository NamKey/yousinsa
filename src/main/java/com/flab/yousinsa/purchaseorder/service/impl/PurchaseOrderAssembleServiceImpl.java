package com.flab.yousinsa.purchaseorder.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.events.SellProductEvent;
import com.flab.yousinsa.product.service.component.SellProductEventPublisher;
import com.flab.yousinsa.product.service.contract.ProductOptionReadService;
import com.flab.yousinsa.product.service.contract.ProductOptionStockService;
import com.flab.yousinsa.product.service.contract.ProductOptionUpdateService;
import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderCreateService;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.service.contract.UserReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseOrderAssembleServiceImpl implements PurchaseOrderService {

	private final ProductOptionUpdateService productOptionUpdateService;
	private final ProductOptionReadService productOptionReadService;
	private final ProductOptionStockService productOptionStockService;
	private final UserReadService userReadService;
	private final PurchaseOrderCreateService purchaseOrderCreateService;
	private final SellProductEventPublisher sellProductEventPublisher;

	@Transactional
	@Override
	public Long createPurchaseOrder(CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto, AuthUser user) {
		Assert.notNull(createPurchaseOrderRequestDto, "purchaseOrder request must not be null");

		Long productOptionId = createPurchaseOrderRequestDto.getProductOptionId();
		Integer purchaseOrderAmount = createPurchaseOrderRequestDto.getPurchaseOrderAmount();
		productOptionUpdateService.sellProduct(
			productOptionId,
			purchaseOrderAmount
		);

		UserEntity buyer = userReadService.getUser(user.getId());
		ProductOptionEntity requestProductOption = productOptionReadService.getProductOption(productOptionId);

		return purchaseOrderCreateService.createPurchaseOrder(
			buyer,
			requestProductOption,
			purchaseOrderAmount
		);
	}

	@Transactional
	@Override
	public Long submitPurchaseOrder(
		CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto,
		AuthUser user
	) {
		Assert.notNull(createPurchaseOrderRequestDto, "purchaseOrder request must not be null");
		Long productOptionId = createPurchaseOrderRequestDto.getProductOptionId();
		Integer purchaseOrderAmount = createPurchaseOrderRequestDto.getPurchaseOrderAmount();

		ProductOptionEntity requestProductOption = productOptionReadService.getProductOption(productOptionId);
		Integer remainedStock = productOptionStockService.tryDeductProductStock(
			requestProductOption,
			purchaseOrderAmount
		);

		UserEntity buyer = userReadService.getUser(user.getId());

		Long requestedPurchaseOrderId = purchaseOrderCreateService.createPurchaseOrder(
			buyer,
			requestProductOption,
			purchaseOrderAmount
		);

		sellProductEventPublisher.publishSellProductEvent(
			SellProductEvent.builder()
				.soldProductOptionId(productOptionId)
				.purchaseAmount(purchaseOrderAmount)
				.purchaseOrderId(requestedPurchaseOrderId)
				.remainedStock(remainedStock)
				.eventPublishedTime(LocalDateTime.now())
				.build()
		);

		return requestedPurchaseOrderId;
	}
}
