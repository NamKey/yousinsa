package com.flab.yousinsa.product.service.impl;

import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.global.exceptions.IllegalResourceConditionException;
import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.product.domain.dtos.ProductCreateOptionDto;
import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.repository.contract.ProductRepository;
import com.flab.yousinsa.product.service.converter.ProductCreateRequestConverter;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.store.exceptions.IllegalStoreAccessException;
import com.flab.yousinsa.store.v1.service.StoreReadService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith(MockitoExtension.class)
class ProductCreateServiceImplTest {

	@Mock
	ProductRepository productRepository;

	@Mock
	ProductCreateRequestConverter productCreateRequestConverter;

	@Mock
	StoreReadService storeReadService;

	@InjectMocks
	ProductCreateServiceImpl productCreateService;

	AuthUser ownerOfStore;
	AuthUser notOwnerOfStore;
	ProductEntity savedProduct;
	UserEntity owner;
	Store store;
	Store notAcceptedStore;
	ProductCreateRequestDto productCreateRequestDto;

	@BeforeEach
	public void setUp() {
		ownerOfStore = new AuthUser(1L, "ownerOfStore", "ownerOfStore@yousinsa.com", UserRole.STORE_OWNER);
		notOwnerOfStore = new AuthUser(2L, "notOwnerOfStore", "notOwnerOfStore@yousinsa.com", UserRole.STORE_OWNER);

		ProductOptionEntity small = new ProductOptionEntity(1L, null, 5, "small");
		ProductOptionEntity medium = new ProductOptionEntity(2L, null, 10, "medium");
		ProductOptionEntity large = new ProductOptionEntity(3L, null, 15, "large");
		List<ProductOptionEntity> options = new ArrayList<>();

		options.add(small);
		options.add(medium);
		options.add(large);

		ProductCreateOptionDto smallDto = new ProductCreateOptionDto(5, "small");
		ProductCreateOptionDto mediumDto = new ProductCreateOptionDto(10, "medium");
		ProductCreateOptionDto largeDto = new ProductCreateOptionDto(15, "large");

		List<ProductCreateOptionDto> productOptionDtoList = new ArrayList<>();
		productOptionDtoList.add(smallDto);
		productOptionDtoList.add(mediumDto);
		productOptionDtoList.add(largeDto);

		ProductCategory topCategory = ProductCategory.TOP;
		long productPrice = 1000L;

		savedProduct = ProductEntity.builder()
			.id(1L)
			.category(topCategory)
			.productPrice(productPrice)
			.store(store)
			.options(options)
			.build();

		owner = new UserEntity(1L, "ownerOfStore", "ownerOfStore@yousinsa.com", "password", UserRole.STORE_OWNER, null);
		store = new Store(1L, "newStore", StoreStatus.ACCEPTED);
		store.addStoreOwner(owner);
		notAcceptedStore = new Store(2L, "notAcceptedStore", StoreStatus.REQUESTED);
		notAcceptedStore.addStoreOwner(owner);

		productCreateRequestDto = new ProductCreateRequestDto(
			store.getId(),
			"newProduct",
			productOptionDtoList,
			topCategory,
			productPrice
		);
	}

	@UnitTest
	@Test
	@DisplayName("Owner가 아닌 다른 User가 Store에 물품을 등록하려 하면 실패")
	public void createProductByNotOwnerOfStore() {
		given(storeReadService.getStoreByOwner(anyLong(), any(AuthUser.class))).willThrow(
			new IllegalStoreAccessException("this store feature only for owner"));

		Assertions.assertThatThrownBy(
				() -> productCreateService.createProduct(productCreateRequestDto, notOwnerOfStore)
			)
			.isInstanceOf(IllegalStoreAccessException.class)
			.hasMessageContaining("this store feature only for owner");

		then(storeReadService).should().getStoreByOwner(store.getId(), notOwnerOfStore);
	}

	@UnitTest
	@Test
	@DisplayName("올바르지 않은 Store에 물품 등록 요청을 하면 실패")
	public void createProductWithInvalidStore() {
		given(storeReadService.getStoreByOwner(anyLong(), any(AuthUser.class))).willThrow(
			new NotFoundException("requested store id does not exist"));

		Assertions.assertThatThrownBy(
				() -> productCreateService.createProduct(productCreateRequestDto, ownerOfStore)
			)
			.isInstanceOf(NotFoundException.class)
			.hasMessageContaining("requested store id does not exist");

		then(storeReadService).should().getStoreByOwner(store.getId(), ownerOfStore);
	}

	@UnitTest
	@Test
	@DisplayName("로그인한 User가 Owner로 있는 Store에 물품 등록하면 성공")
	public void createProductWithValidStoreByOwner() {
		given(storeReadService.getStoreByOwner(anyLong(), any(AuthUser.class))).willReturn(store);

		given(productRepository.save(any(ProductEntity.class))).willReturn(savedProduct);
		given(productCreateRequestConverter.convertDtoToProductEntity(any(ProductCreateRequestDto.class),
			any(Store.class))).willReturn(savedProduct);

		Long createdProductId = productCreateService.createProduct(productCreateRequestDto, ownerOfStore);

		Assertions.assertThat(createdProductId).isEqualTo(savedProduct.getId());

		then(storeReadService).should()
			.getStoreByOwner(eq(productCreateRequestDto.getRequestStoreId()), refEq(ownerOfStore));
		then(productRepository).should().save(eq(savedProduct));
		then(productCreateRequestConverter).should()
			.convertDtoToProductEntity(eq(productCreateRequestDto), refEq(store));
	}

	@UnitTest
	@Test
	@DisplayName("Store가 입점 수락된 상태가 아니라면 Store에 물품 등록 실패")
	public void createProductWithStoreStatusIsNotAccepted() {
		// given
		given(storeReadService.getStoreByOwner(anyLong(), any(AuthUser.class))).willReturn(notAcceptedStore);

		// when
		Assertions.assertThatThrownBy(
				() -> productCreateService.createProduct(productCreateRequestDto, ownerOfStore)
			)
			.isInstanceOf(IllegalResourceConditionException.class)
			.hasMessageContaining("Store must be accepted for creating product");

		then(storeReadService).should()
			.getStoreByOwner(eq(productCreateRequestDto.getRequestStoreId()), refEq(ownerOfStore));
	}
}
