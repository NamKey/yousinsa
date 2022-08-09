package com.flab.yousinsa.product.service.contract;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;

public interface ProductOptionReadService {
	ProductOptionEntity getProductOption(Long productId);
}
