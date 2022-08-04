package com.flab.yousinsa.product.domain.dtos;

import java.util.List;

import com.flab.yousinsa.product.domain.enums.ProductCategory;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ProductCreateRequestDto {
	private Long requestStoreId;

	private String productName;

	private List<ProductCreateOptionDto> productCreateOptions;

	private ProductCategory productCategory;

	private Long productPrice;
}
