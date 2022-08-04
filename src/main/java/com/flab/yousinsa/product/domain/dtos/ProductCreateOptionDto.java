package com.flab.yousinsa.product.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateOptionDto {
	private Integer productCount;

	private String productSize;
}
