package com.flab.yousinsa.product.controller;

import static com.flab.yousinsa.ApiDocumentUtils.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.product.domain.dtos.ProductDto;
import com.flab.yousinsa.product.domain.dtos.ProductOptionDto;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.service.contract.ProductCreateService;
import com.flab.yousinsa.product.service.contract.ProductGetService;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(ProductController.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerGetProductsTestDoc {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	ProductGetService productGetService;

	@MockBean
	ProductCreateService productCreateService;

	ProductEntity productTopBlueShirts;
	ProductEntity productTopShirts;
	ProductEntity productTopPinkShirts;

	ProductDto productTopBlueShirtsDto;
	ProductDto productTopShirtsDto;
	ProductDto productTopPinkShirtsDto;

	@BeforeEach
	public void setUp() {
		productTopBlueShirts = makeProductEntity(1L, "productTopBlueShirts", ProductCategory.TOP, 1000L);
		productTopShirts = makeProductEntity(2L, "productTopShirts", ProductCategory.TOP, 1500L);
		productTopPinkShirts = makeProductEntity(3L, "productTopPinkShirts", ProductCategory.TOP, 2000L);

		productTopBlueShirtsDto = makeProductDto(1L, "productTopBlueShirtsDto", ProductCategory.TOP, 1000L);
		productTopShirtsDto = makeProductDto(2L, "productTopShirtsDto", ProductCategory.TOP, 1500L);
		productTopPinkShirtsDto = makeProductDto(3L, "productTopPinkShirtsDto", ProductCategory.TOP, 2000L);
	}

	@UnitTest
	@Test
	@DisplayName("물품 목록 요청 API Doc")
	public void getProductsDoc() throws Exception {
		// given
		List<ProductDto> products = new ArrayList<>();
		products.add(productTopBlueShirtsDto);
		products.add(productTopShirtsDto);
		products.add(productTopPinkShirtsDto);

		Pageable pageable = PageRequest.of(0, 5, Sort.by("id").ascending());
		PageImpl<ProductDto> productDtoPage = new PageImpl<>(products, pageable, 3);
		given(productGetService.getProductsByCategory(any(ProductCategory.class), any(Pageable.class)))
			.willReturn(productDtoPage);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.get("/api/v1/products")
				.param("category", String.valueOf(ProductCategory.TOP))
				.param("page", String.valueOf(pageable.getPageNumber()))
				.param("size", String.valueOf(pageable.getPageSize()))
				.param("sort", String.valueOf(pageable.getSort()))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isOk())
			.andExpect(content().string(objectMapper.writeValueAsString(productDtoPage)))
			.andDo(
				document("product-get",
					getDocumentRequest(),
					getDocumentResponse(),
					requestParameters(
						parameterWithName("category").description("보여줄 카테고리 명"),
						parameterWithName("page").description("0부터 시작하는 page index"),
						parameterWithName("size").description("page안에 들어 있는 최대 물품 수"),
						parameterWithName("sort").description("정렬 기준(정렬하고자 하는 필드, 오름차순)")
					),
					responseFields(
						fieldWithPath("content").type(JsonFieldType.ARRAY).description("요청한 Category에 해당하는 Product 리스트"),
						fieldWithPath("content[].productId").type(JsonFieldType.NUMBER).description("상품 ID(PK)"),
						fieldWithPath("content[].productName").type(JsonFieldType.STRING).description("상품 이름"),
						fieldWithPath("content[].productCategory").type(JsonFieldType.STRING).description("상품 Category"),
						fieldWithPath("content[].productPrice").type(JsonFieldType.NUMBER).description("상품 가격"),
						fieldWithPath("content[].productOptions").type(JsonFieldType.ARRAY).description("상품이 갖고 있는 Option 리스트"),
						fieldWithPath("content[].productOptions[].productOptionId").type(JsonFieldType.NUMBER).description("상품이 갖고 있는 Option ID(PK)"),
						fieldWithPath("content[].productOptions[].productSize").type(JsonFieldType.STRING).description("상품이 갖고 있는 Option Size 명"),
						fieldWithPath("content[].productOptions[].productCount").type(JsonFieldType.NUMBER).description("상품이 갖고 있는 Option의 남아있는 수량"),
						fieldWithPath("pageable").type(JsonFieldType.OBJECT).description("페이지 정보"),
						fieldWithPath("totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
						fieldWithPath("totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
						fieldWithPath("pageable.sort.unsorted").ignored(),
						fieldWithPath("pageable.sort.sorted").ignored(),
						fieldWithPath("pageable.sort.empty").ignored(),
						fieldWithPath("pageable.pageNumber").type(JsonFieldType.NUMBER).description("요청한 Page 번호"),
						fieldWithPath("pageable.pageSize").type(JsonFieldType.NUMBER).description("요청한 Page 크기"),
						fieldWithPath("pageable.offset").ignored(),
						fieldWithPath("pageable.paged").ignored(),
						fieldWithPath("pageable.unpaged").ignored(),
						fieldWithPath("last").ignored(),
						fieldWithPath("numberOfElements").ignored(),
						fieldWithPath("size").ignored(),
						fieldWithPath("number").ignored(),
						fieldWithPath("first").ignored(),
						fieldWithPath("sort").ignored(),
						fieldWithPath("sort.unsorted").ignored(),
						fieldWithPath("sort.sorted").ignored(),
						fieldWithPath("sort.empty").ignored(),
						fieldWithPath("empty").type(JsonFieldType.BOOLEAN).description("비어있는 페이지 여부")
					)
				)
			)
			.andDo(print());

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
