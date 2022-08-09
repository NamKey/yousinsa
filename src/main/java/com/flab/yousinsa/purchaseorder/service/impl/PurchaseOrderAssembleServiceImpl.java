package com.flab.yousinsa.purchaseorder.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.flab.yousinsa.product.service.contract.ProductOptionReadService;
import com.flab.yousinsa.product.service.contract.ProductOptionUpdateService;
import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderEntity;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderItemEntity;
import com.flab.yousinsa.purchaseorder.domain.enums.PurchaseOrderStatus;
import com.flab.yousinsa.purchaseorder.repository.contract.PurchaseOrderRepository;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.service.contract.UserReadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PurchaseOrderAssembleServiceImpl implements PurchaseOrderService {

	private final ProductOptionUpdateService productOptionUpdateService;
	private final ProductOptionReadService productOptionReadService;
	private final UserReadService userReadService;

	private final PurchaseOrderRepository purchaseOrderRepository;

	@Transactional
	@Override
	public Long createPurchaseOrder(CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto, AuthUser user) {
		Assert.notNull(createPurchaseOrderRequestDto, "purchaseOrder request must not be null");

		UserEntity buyer = userReadService.getUser(user.getId());

		PurchaseOrderEntity purchaseOrder = PurchaseOrderEntity.builder()
			.buyer(buyer)
			.purchaseOrderStatus(PurchaseOrderStatus.ACCEPTED)
			.build();

		PurchaseOrderItemEntity purchaseOrderItem = PurchaseOrderItemEntity.builder()
			.purchaseOrder(purchaseOrder)
			.purchaseOrderAmount(createPurchaseOrderRequestDto.getPurchaseOrderAmount())
			.productOption(
				productOptionReadService.getProductOption(createPurchaseOrderRequestDto.getProductOptionId()))
			.build();

		productOptionUpdateService.sellProduct(
			purchaseOrderItem.getProductOption().getId(),
			purchaseOrderItem.getPurchaseOrderAmount()
		);
		purchaseOrder.addPurchaseOrderItem(purchaseOrderItem);

		PurchaseOrderEntity acceptedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

		return acceptedPurchaseOrder.getId();
	}
}
