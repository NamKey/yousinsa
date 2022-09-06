package com.flab.yousinsa.product.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.product.domain.dtos.ProductCreateOptionDto;
import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.service.contract.ProductCreateService;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.store.exceptions.IllegalStoreAccessException;
import com.flab.yousinsa.user.controller.aop.AuthenticateAspect;
import com.flab.yousinsa.user.controller.config.SpringTestAopConfig;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = SpringTestAopConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private ProductCreateService productCreateService;

	UserEntity owner;
	AuthUser ownerAuth;
	AuthUser notOwnerAuth;
	Store store;
	ProductEntity product;
	ProductOptionEntity smallOption;
	ProductOptionEntity mediumOption;
	ProductOptionEntity largeOption;
	ProductCreateRequestDto productCreateRequestDto;

	@BeforeEach
	public void setUp() {
		owner = UserEntity.builder()
			.id(1L)
			.store(store)
			.userEmail("owner@yousinsa.com")
			.userName("owner")
			.userRole(UserRole.STORE_OWNER)
			.build();
		ownerAuth = new AuthUser(owner.getId(), owner.getUserName(), owner.getUserEmail(), owner.getUserRole());
		notOwnerAuth = new AuthUser(2L, "notOwner", "notOwner@yousinsa.com", UserRole.STORE_OWNER);
		store = Store.builder()
			.id(1L)
			.storeName("yousinsaStore")
			.storeOwner(owner)
			.storeStatus(StoreStatus.ACCEPTED)
			.build();

		ProductCreateOptionDto smallOptionDto = new ProductCreateOptionDto(5, "small");
		ProductCreateOptionDto mediumOptionDto = new ProductCreateOptionDto(10, "medium");
		ProductCreateOptionDto largeOptionDto = new ProductCreateOptionDto(15, "large");

		List<ProductCreateOptionDto> productOptionDtos = new ArrayList<>();
		productOptionDtos.add(smallOptionDto);
		productOptionDtos.add(mediumOptionDto);
		productOptionDtos.add(largeOptionDto);

		productCreateRequestDto = ProductCreateRequestDto.builder()
			.requestStoreId(store.getId())
			.productCategory(ProductCategory.TOP)
			.productPrice(1000L)
			.productCreateOptions(productOptionDtos)
			.build();

		smallOption = ProductOptionEntity.builder()
			.id(1L)
			.productCount(5)
			.productSize("small")
			.product(product)
			.build();
		mediumOption = ProductOptionEntity.builder()
			.id(2L)
			.productCount(10)
			.productSize("medium")
			.product(product)
			.build();
		largeOption = ProductOptionEntity.builder()
			.id(3L)
			.productCount(15)
			.productSize("large")
			.product(product)
			.build();
		List<ProductOptionEntity> productOptions = new ArrayList<>();
		productOptions.add(smallOption);
		productOptions.add(mediumOption);
		productOptions.add(largeOption);

		product = ProductEntity.builder()
			.id(1L)
			.productPrice(1000L)
			.category(ProductCategory.TOP)
			.store(store)
			.options(productOptions)
			.build();
	}

	@UnitTest
	@Test
	@DisplayName("해당 Store의 Owner가 아닌 Store에 물품 등록하는 경우 실패")
	public void createProductWithNotOwnerOfStore() throws Exception {
		// given
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(AuthenticateAspect.AUTH_USER, notOwnerAuth);
		given(productCreateService.createProduct(any(ProductCreateRequestDto.class), any(AuthUser.class)))
			.willThrow(new IllegalStoreAccessException("this store feature only for owner"));

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/products")
				.session(mockHttpSession)
				.content(objectMapper.writeValueAsString(productCreateRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect(result -> Assertions.assertThat(result.getResolvedException())
				.isInstanceOf(IllegalStoreAccessException.class)
				.hasMessageContaining("this store feature only for owner")
			);
	}

	@UnitTest
	@Test
	@DisplayName("존재하지 않는 Store Store에 물품 등록하는 경우 실패")
	public void createProductWithNotExistOfStore() throws Exception {
		// given
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(AuthenticateAspect.AUTH_USER, ownerAuth);
		given(productCreateService.createProduct(any(ProductCreateRequestDto.class), any(AuthUser.class)))
			.willThrow(new NotFoundException("requested store id does not exist"));

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/products")
				.session(mockHttpSession)
				.content(objectMapper.writeValueAsString(productCreateRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isNotFound())
			.andExpect(result -> Assertions.assertThat(result.getResolvedException())
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("requested store id does not exist")
			);
	}

	@UnitTest
	@Test
	@DisplayName("아직 입점 처리 되지 않은 Store에 물품 등록하는 경우 실패")
	public void createProductWithNotAcceptedStore() throws Exception {
		// given
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(AuthenticateAspect.AUTH_USER, ownerAuth);
		given(productCreateService.createProduct(any(ProductCreateRequestDto.class), any(AuthUser.class)))
			.willThrow(new NotFoundException("requested store id does not exist"));

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/products")
				.session(mockHttpSession)
				.content(objectMapper.writeValueAsString(productCreateRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isNotFound())
			.andExpect(result -> Assertions.assertThat(result.getResolvedException())
				.isInstanceOf(NotFoundException.class)
				.hasMessageContaining("requested store id does not exist")
			);
	}
}
