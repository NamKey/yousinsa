package com.flab.yousinsa.product.service.contract;

import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

public interface ProductCreateService {
	Long createProduct(ProductCreateRequestDto productCreateRequest, AuthUser user);
}
