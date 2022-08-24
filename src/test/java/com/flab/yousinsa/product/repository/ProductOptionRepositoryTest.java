package com.flab.yousinsa.product.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.repository.contract.ProductRepository;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.domain.StoreRepository;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;
import com.flab.yousinsa.user.repository.contract.UserRepository;

@DataJpaTest
class ProductOptionRepositoryTest {

	@Autowired
	ProductOptionRepository productOptionRepository;

	@Autowired
	UserRepository userRepository;

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	ProductRepository productRepository;

	Store store;
	UserEntity owner;
	ProductEntity product;
	ProductOptionEntity productOption;
	Long requestOptionId;

	@BeforeEach
	public void setUp() {
		requestOptionId = 1L;


		owner = UserEntity.builder()
			.userName("owner")
			.userEmail("owner@yousinsa.com")
			.userPassword("password")
			.userRole(UserRole.STORE_OWNER)
			.build();
		userRepository.save(owner);

		store = Store.builder()
			.storeName("newStore")
			.storeStatus(StoreStatus.ACCEPTED)
			.build();
		store.addStoreOwner(owner);
		storeRepository.save(store);

		List<ProductOptionEntity> productOptions = new ArrayList<>();
		product = ProductEntity.builder()
			.productName("newProduct")
			.productPrice(1000L)
			.category(ProductCategory.TOP)
			.options(productOptions)
			.store(store)
			.build();

		productOption = ProductOptionEntity.builder()
			.product(product)
			.productCount(1000)
			.productSize("free")
			.build();
		productOptions.add(productOption);
		productRepository.save(product);
	}

	@Test
	@DisplayName("Explicit Lock을 통해 ProductOption SELECT")
	public void findProductOptionWithLock() {
		// given

		// when
		Optional<ProductOptionEntity> foundProductOption = productOptionRepository.findByIdWithLock(1L);

		// then
		Assertions.assertThat(foundProductOption).hasValue(productOption);
	}
}
