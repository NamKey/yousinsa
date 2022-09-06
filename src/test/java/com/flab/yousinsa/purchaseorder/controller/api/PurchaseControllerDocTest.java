package com.flab.yousinsa.purchaseorder.controller.api;

import static com.flab.yousinsa.ApiDocumentUtils.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.controller.aop.AuthenticateAspect;
import com.flab.yousinsa.user.controller.config.SpringTestAopConfig;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PurchaseController.class)
@ContextConfiguration(classes = SpringTestAopConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
class PurchaseControllerDocTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PurchaseOrderService purchaseOrderService;

	AuthUser buyer;

	CreatePurchaseOrderRequestDto purchaseOrderRequestDto;

	@BeforeEach
	public void setUp() {
		buyer = new AuthUser(1L, "buyer", "buyer@yousinsa.com", UserRole.BUYER);

		purchaseOrderRequestDto = new CreatePurchaseOrderRequestDto(1L, 10);
	}

	@UnitTest
	@Test
	@DisplayName("구매 주문 생성 요청 API Doc")
	public void createPurchaseOrderDoc() throws Exception {
		// given
		long createdPurchaseOrderId = 1L;
		MockHttpSession httpSession = new MockHttpSession();
		httpSession.setAttribute(AuthenticateAspect.AUTH_USER, buyer);

		given(purchaseOrderService.createPurchaseOrder(
			any(CreatePurchaseOrderRequestDto.class),
			any(AuthUser.class))
		).willReturn(createdPurchaseOrderId);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.post("/api/v1/orders").session(httpSession)
				.content(objectMapper.writeValueAsString(purchaseOrderRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isCreated())
			.andExpect(header().exists(HttpHeaders.LOCATION))
			.andExpect(header().string(HttpHeaders.LOCATION, "api/v1/orders/" + createdPurchaseOrderId))
			.andDo(
				document("order-create",
					getDocumentRequest(),
					getDocumentResponse(),
					requestFields(
						fieldWithPath("productOptionId").type(JsonFieldType.NUMBER).description("구매 주문하고자 하는 상품 옵션 Id"),
						fieldWithPath("purchaseOrderAmount").type(JsonFieldType.NUMBER).description("구매하고자 하는 상품의 갯수")
					),
					responseHeaders(
						headerWithName(HttpHeaders.LOCATION).description("주문된 구매 주문의 경로")
					)
				)
			);
	}
}
