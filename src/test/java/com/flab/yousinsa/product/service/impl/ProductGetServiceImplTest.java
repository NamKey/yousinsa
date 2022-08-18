package com.flab.yousinsa.product.service.impl;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.product.domain.dtos.ProductDto;
import com.flab.yousinsa.product.domain.dtos.ProductOptionDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.repository.contract.ProductRepository;
import com.flab.yousinsa.product.service.component.ProductDtoConverter;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith(MockitoExtension.class)
class ProductGetServiceImplTest {

	@Mock
	ProductRepository productRepository;

	@Mock
	ProductDtoConverter productDtoConverter;

	@InjectMocks
	ProductGetServiceImpl productGetService;

	UserEntity owner;
	Store store;
	ProductEntity productTopBlueShirts;
	ProductEntity productTopShirts;
	ProductEntity productTopPinkShirts;

	ProductDto productTopBlueShirtsDto;
	ProductDto productTopShirtsDto;
	ProductDto productTopPinkShirtsDto;

	@BeforeEach
	public void setUp() {
		owner = UserEntity.builder()
			.id(1L)
			.userName("owner")
			.userEmail("owner@yousinsa.com")
			.userRole(UserRole.STORE_OWNER)
			.userPassword("hashedPassword")
			.store(store)
			.build();

		store = Store.builder()
			.storeStatus(StoreStatus.ACCEPTED)
			.storeName("yousinsaStore")
			.build();
		store.addStoreOwner(owner);

		productTopBlueShirts = makeProductEntity(1L, "productTopBlueShirts", ProductCategory.TOP, 1000L);
		productTopShirts = makeProductEntity(2L, "productTopShirts", ProductCategory.TOP, 1500L);
		productTopPinkShirts = makeProductEntity(3L, "productTopPinkShirts", ProductCategory.TOP, 2000L);

		productTopBlueShirtsDto = makeProductDto(1L, "productTopBlueShirtsDto", ProductCategory.TOP, 1000L);
		productTopShirtsDto = makeProductDto(2L, "productTopShirtsDto", ProductCategory.TOP, 1500L);
		productTopPinkShirtsDto = makeProductDto(3L, "productTopPinkShirtsDto", ProductCategory.TOP, 2000L);
	}

	@UnitTest
	@Test
	@DisplayName("상품이 없는 Category에 대해서 요청시 비어있는 Page 반환")
	public void getProductsWithNoItemCategory() throws Exception {
		// given
		Pageable pageable = PageRequest.of(5, 5);

		given(productRepository.findAllByCategory(any(ProductCategory.class), any(Pageable.class))).willReturn(
			Page.empty());

		// when
		Page<ProductDto> productsByCategory = productGetService.getProductsByCategory(ProductCategory.TOP, pageable);

		// then
		assertThat(productsByCategory.getContent().size()).isEqualTo(0L);
		assertThat(productsByCategory.getTotalPages()).isEqualTo(1);

		then(productRepository).should().findAllByCategory(eq(ProductCategory.TOP), refEq(pageable));
	}

	@UnitTest
	@Test
	@DisplayName("Category에 대해서 상품이 있다면 Page를 반환")
	public void getProductsWithNotExistCategory() throws Exception {
		// given
		List<ProductEntity> products = new ArrayList<>();
		products.add(productTopBlueShirts);
		products.add(productTopShirts);
		products.add(productTopPinkShirts);
		PageImpl<ProductEntity> productEntityPage = new PageImpl<>(products);

		given(productRepository.findAllByCategory(any(ProductCategory.class), any(Pageable.class)))
			.willReturn(productEntityPage);
		given(productDtoConverter.convertProductEntityToProductDto(productTopBlueShirts))
			.willReturn(productTopBlueShirtsDto);
		given(productDtoConverter.convertProductEntityToProductDto(productTopShirts))
			.willReturn(productTopShirtsDto);
		given(productDtoConverter.convertProductEntityToProductDto(productTopPinkShirts))
			.willReturn(productTopPinkShirtsDto);
		Pageable pageable = PageRequest.of(5, 5);


		// when
		Page<ProductDto> productsByCategory = productGetService.getProductsByCategory(ProductCategory.TOP, pageable);

		// then
		assertThat(productsByCategory.getContent().size()).isEqualTo(3L);
		assertThat(productsByCategory.getTotalPages()).isEqualTo(1);

		then(productRepository).should().findAllByCategory(eq(ProductCategory.TOP), refEq(pageable));
	}

	private ProductDto makeProductDto(
		Long productId,
		String productName,
		ProductCategory productCategory,
		Long productPrice
	) {
		List<ProductOptionDto> productOptionDtos = new ArrayList<>();
		ProductDto productDto = ProductDto.builder()
			.productId(productId)
			.productName(productName)
			.productCategory(productCategory)
			.productPrice(productPrice)
			.productOptions(productOptionDtos)
			.build();

		ProductOptionDto smallOptionDto = ProductOptionDto.builder()
			.productOptionId(1L)
			.productSize("small")
			.productCount(5)
			.build();
		ProductOptionDto mediumOptionDto = ProductOptionDto.builder()
			.productOptionId(2L)
			.productSize("medium")
			.productCount(10)
			.build();
		ProductOptionDto largeOptionDto = ProductOptionDto.builder()
			.productOptionId(3L)
			.productSize("large")
			.productCount(15)
			.build();
		productOptionDtos.add(smallOptionDto);
		productOptionDtos.add(mediumOptionDto);
		productOptionDtos.add(largeOptionDto);

		return productDto;
	}

	private ProductEntity makeProductEntity(Long productId, String productName, ProductCategory productCategory,
		Long productPrice) {
		List<ProductOptionEntity> productOptions = new ArrayList<>();
		ProductEntity product = ProductEntity.builder()
			.id(productId)
			.productName(productName)
			.category(productCategory)
			.productPrice(productPrice)
			.options(productOptions)
			.build();

		ProductOptionEntity smallOption = ProductOptionEntity.builder()
			.id(1L)
			.product(product)
			.productSize("small")
			.productCount(5)
			.build();
		ProductOptionEntity mediumOption = ProductOptionEntity.builder()
			.id(2L)
			.product(product)
			.productSize("medium")
			.productCount(10)
			.build();
		ProductOptionEntity largeOption = ProductOptionEntity.builder()
			.id(3L)
			.product(product)
			.productSize("large")
			.productCount(15)
			.build();
		productOptions.add(smallOption);
		productOptions.add(mediumOption);
		productOptions.add(largeOption);

		return product;
	}
}
