package com.flab.yousinsa.product.service.impl;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.repository.ProductOptionRepository;
import com.flab.yousinsa.product.service.contract.ProductOptionReadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductOptionReadServiceImpl implements ProductOptionReadService {

	private final ProductOptionRepository productOptionRepository;

	@Transactional(readOnly = true)
	@Override
	public ProductOptionEntity getProductOption(Long productOptionId) {
		return productOptionRepository.findById(productOptionId).orElseThrow(() -> {
			throw new NotFoundException("requested product option id does not exist");
		});
	}
}
