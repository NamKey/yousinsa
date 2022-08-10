package com.flab.yousinsa.product.controller;

import static com.flab.yousinsa.ApiDocumentUtils.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.product.domain.dtos.ProductCreateOptionDto;
import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.service.contract.ProductCreateService;
import com.flab.yousinsa.product.service.contract.ProductGetService;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.user.controller.aop.AuthenticateAspect;
import com.flab.yousinsa.user.controller.config.SpringTestAopConfig;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(ProductController.class)
@ContextConfiguration(classes = SpringTestAopConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerCreateProductTestDoc {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	ProductGetService productGetService;

	@MockBean
	private ProductCreateService productCreateService;

	UserEntity owner;
	AuthUser ownerAuth;
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
			.productName("newProductName")
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
	@DisplayName("물품 등록 API Doc")
	public void createProduct() throws Exception {
		// given
		given(productCreateService.createProduct(any(ProductCreateRequestDto.class), any(AuthUser.class)))
			.willReturn(product.getId());
		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(AuthenticateAspect.AUTH_USER, ownerAuth);

		// when
		ResultActions result = mockMvc.perform(
			post("/api/v1/products")
				.session(mockHttpSession)
				.content(objectMapper.writeValueAsString(productCreateRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		result.andExpect(status().isCreated())
			.andExpect(header().exists(HttpHeaders.LOCATION))
			.andExpect(header().stringValues("Location", "/api/v1/products/" + product.getId()))
			.andDo(
				document("product-create",
					getDocumentRequest(),
					getDocumentResponse(),
					requestFields(
						fieldWithPath("requestStoreId").type(JsonFieldType.NUMBER).description("물품을 등록할 Store의 ID"),
						fieldWithPath("productName").type(JsonFieldType.STRING).description("등록할 물품명"),
						fieldWithPath("productCreateOptions[]").description("등록할 물건의 옵션 리스트"),
						fieldWithPath("productCreateOptions[].productCount").description("등록할 물건의 옵션 수량"),
						fieldWithPath("productCreateOptions[].productSize").description("등록할 물건의 옵션 사이즈"),
						fieldWithPath("productCategory").type(JsonFieldType.STRING)
							.description("등록할 물품의 종류(상의, 하의, 외투)"),
						fieldWithPath("productPrice").type(JsonFieldType.NUMBER).description("등록할 물품의 가격")
					),
					responseHeaders(
						headerWithName(HttpHeaders.LOCATION).description("등록한 물품의 URI 경로")
					)
				)
			);

		then(productCreateService).should()
			.createProduct(refEq(productCreateRequestDto, "productCreateOptions"), refEq(ownerAuth));
	}
}
