package com.flab.yousinsa.product.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.global.exceptions.IllegalResourceConditionException;
import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.repository.contract.ProductRepository;
import com.flab.yousinsa.product.service.contract.ProductCreateService;
import com.flab.yousinsa.product.service.converter.ProductCreateRequestConverter;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.store.v1.service.StoreReadService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProductCreateServiceImpl implements ProductCreateService {

	private final ProductRepository productRepository;
	private final ProductCreateRequestConverter productCreateRequestConverter;
	private final StoreReadService storeService;

	@Transactional
	@Override
	public Long createProduct(ProductCreateRequestDto productCreateRequest, AuthUser user) {
		Store requestedStore = storeService.getStoreByOwner(productCreateRequest.getRequestStoreId(), user);

		validateStoreStatusForCreatingProduct(requestedStore);

		ProductEntity newProduct = productCreateRequestConverter
			.convertDtoToProductEntity(productCreateRequest, requestedStore);

		ProductEntity createdProduct = productRepository.save(newProduct);

		return createdProduct.getId();
	}

	private void validateStoreStatusForCreatingProduct(Store store) {
		if (store.getStoreStatus() != StoreStatus.ACCEPTED) {
			throw new IllegalResourceConditionException("Store must be accepted for creating product");
		}
	}
}
