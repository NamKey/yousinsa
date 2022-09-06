package com.flab.yousinsa.product.domain.dtos;

import java.util.List;

import com.flab.yousinsa.product.domain.enums.ProductCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductDto {
	private Long productId;

	private String productName;

	private ProductCategory productCategory;

	private Long productPrice;

	private List<ProductOptionDto> productOptions;
}
