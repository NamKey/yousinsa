package com.flab.yousinsa.purchaseorder.service.contract;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.user.domain.entities.UserEntity;

public interface PurchaseOrderCreateService {
	Long createPurchaseOrder(UserEntity buyer, ProductOptionEntity requestProductOption, Integer purchaseOrderAmount);
}
