package com.flab.yousinsa.product.service.impl;

import org.springframework.stereotype.Service;

import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.service.contract.ProductCreateService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

@Service
public class ProductCreateServiceImpl implements ProductCreateService {
	@Override
	public Long createProduct(ProductCreateRequestDto productCreateRequest, AuthUser user) {
		return null;
	}
}
