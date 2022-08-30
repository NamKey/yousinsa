package com.flab.yousinsa.purchaseorder.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderEntity;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderItemEntity;
import com.flab.yousinsa.purchaseorder.domain.enums.PurchaseOrderStatus;
import com.flab.yousinsa.purchaseorder.repository.contract.PurchaseOrderRepository;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderCreateService;
import com.flab.yousinsa.user.domain.entities.UserEntity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PurchaseOrderCreateServiceImpl implements PurchaseOrderCreateService {

	private final PurchaseOrderRepository purchaseOrderRepository;

	@Transactional
	@Override
	public Long createPurchaseOrder(UserEntity buyer, ProductOptionEntity requestProductOption, Integer purchaseOrderAmount) {

		PurchaseOrderEntity purchaseOrder = PurchaseOrderEntity.builder()
			.buyer(buyer)
			.purchaseOrderStatus(PurchaseOrderStatus.IN_PROGRESS)
			.build();

		PurchaseOrderItemEntity purchaseOrderItem = PurchaseOrderItemEntity.builder()
			.purchaseOrder(purchaseOrder)
			.purchaseOrderAmount(purchaseOrderAmount)
			.productOption(requestProductOption)
			.build();

		purchaseOrder.addPurchaseOrderItem(purchaseOrderItem);

		PurchaseOrderEntity acceptedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

		return acceptedPurchaseOrder.getId();
	}
}
