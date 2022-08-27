package com.flab.yousinsa.product.service.impl;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.product.domain.dtos.ProductDto;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.repository.contract.ProductRepository;
import com.flab.yousinsa.product.service.component.ProductDtoConverter;
import com.flab.yousinsa.product.service.contract.ProductGetService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductGetServiceImpl implements ProductGetService {

	private final ProductRepository productRepository;
	private final ProductDtoConverter productDtoConverter;

	@Cacheable(cacheNames = "products", key = "#productCategory.name() + #pageable.pageNumber + #pageable.pageSize + #pageable.sort")
	@Transactional(readOnly = true)
	@Override
	public Page<ProductDto> getProductsByCategory(ProductCategory productCategory, Pageable pageable) {
		return productRepository.findAllByCategory(productCategory, pageable)
			.map(productDtoConverter::convertProductEntityToProductDto);
	}
}
