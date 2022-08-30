package com.flab.yousinsa.purchaseorder.service.contract;

import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

public interface PurchaseOrderService {
	Long createPurchaseOrder(CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto, AuthUser user);

	Long createPurchaseOrderWithRedis(CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto, AuthUser user);

	Long acceptPurchaseOrderStatus(Long purchaseOrderId);
}
