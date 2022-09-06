package com.flab.yousinsa.product.service.converter;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.flab.yousinsa.product.domain.dtos.ProductCreateOptionDto;
import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.store.domain.Store;

@Component
public class ProductCreateRequestConverter {
	public ProductEntity convertDtoToProductEntity(ProductCreateRequestDto productCreateRequestDto, Store store) {
		return ProductEntity.builder()
			.store(store)
			.category(productCreateRequestDto.getProductCategory())
			.productPrice(productCreateRequestDto.getProductPrice())
			.options(
				productCreateRequestDto.getProductCreateOptions().stream()
					.map(this::convertDtoToProductOptionEntity)
					.collect(Collectors.toList())
			)
			.build();
	}

	private ProductOptionEntity convertDtoToProductOptionEntity(ProductCreateOptionDto productCreateOptionDto) {
		return ProductOptionEntity.builder()
			.productCount(productCreateOptionDto.getProductCount())
			.productSize(productCreateOptionDto.getProductSize())
			.build();
	}
}
