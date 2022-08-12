package com.flab.yousinsa.product.service.component;

import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.flab.yousinsa.product.domain.dtos.ProductDto;
import com.flab.yousinsa.product.domain.dtos.ProductOptionDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;

@Component
public class ProductDtoConverter {
	public ProductDto convertProductEntityToProductDto(ProductEntity productEntity) {
		return ProductDto.builder()
			.productId(productEntity.getId())
			.productName(productEntity.getProductName())
			.productCategory(productEntity.getCategory())
			.productPrice(productEntity.getProductPrice())
			.productOptions(
				productEntity.getOptions().stream()
					.map(this::convertProductOptionEntityToProductOptionDto)
					.collect(Collectors.toList()))
			.build();
	}

	public ProductOptionDto convertProductOptionEntityToProductOptionDto(ProductOptionEntity productOptionEntity) {
		return ProductOptionDto.builder()
			.productOptionId(productOptionEntity.getId())
			.productSize(productOptionEntity.getProductSize())
			.productCount(productOptionEntity.getProductCount())
			.build();
	}
}
