package com.flab.yousinsa.product.service.contract;

public interface ProductOptionUpdateService {
	Long sellProduct(Long productOptionId, int purchaseAmount);

	void deductProductOptionCount(Long productOptionId, int purchaseAmount);
}
