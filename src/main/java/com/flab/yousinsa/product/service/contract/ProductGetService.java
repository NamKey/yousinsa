package com.flab.yousinsa.product.service.contract;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.flab.yousinsa.product.domain.dtos.ProductDto;
import com.flab.yousinsa.product.domain.enums.ProductCategory;

public interface ProductGetService {
	Page<ProductDto> getProductsByCategory(ProductCategory productCategory, Pageable pageable);
}
