package com.flab.yousinsa.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.repository.ProductOptionRepository;
import com.flab.yousinsa.product.service.contract.ProductOptionUpdateService;
import com.flab.yousinsa.product.service.exception.IllegalPurchaseRequestException;
import com.flab.yousinsa.product.service.exception.OutOfStockException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductOptionUpdateServiceImpl implements ProductOptionUpdateService {

	private final ProductOptionRepository productOptionRepository;

	@Transactional
	@Override
	public Long sellProduct(Long productOptionId, int purchaseAmount) {
		Assert.notNull(productOptionId, "product option id must be not null");
		validatePurchaseRequest(purchaseAmount);

		ProductOptionEntity requestedProductOption = productOptionRepository.findByIdWithLock(productOptionId)
			.orElseThrow(() -> new NotFoundException("requested product option id does not exist"));

		validateProductStock(requestedProductOption.getProductCount(), purchaseAmount);

		requestedProductOption.sell(purchaseAmount);

		return requestedProductOption.getId();
	}

	@Override
	public void deductProductOption(Long productOptionId, int purchaseAmount) {
		Assert.notNull(productOptionId, "product option id must be not null");
		validatePurchaseRequest(purchaseAmount);

		productOptionRepository.updateProductOptionCount(productOptionId, purchaseAmount);
	}

	private void validatePurchaseRequest(int purchaseAmount) {
		if (purchaseAmount < 1) {
			throw new IllegalPurchaseRequestException("purchase amount can not request under 0");
		}
	}

	private void validateProductStock(int productStockCount, int purchaseAmount) {
		if (productStockCount < 1) {
			throw new OutOfStockException("requested product is out of stock");
		}

		if (productStockCount < purchaseAmount) {
			throw new OutOfStockException("requested purchase amount is over product stock count");
		}
	}
}
