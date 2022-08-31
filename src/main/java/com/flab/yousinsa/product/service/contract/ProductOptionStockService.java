package com.flab.yousinsa.product.service.contract;

public interface ProductOptionStockService {
	Long tryDeductProductStock(Long productOptionId, Integer currentStock,
		Integer purchaseAmount, Long purchaseOrderId);
}
