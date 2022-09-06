package com.flab.yousinsa.purchaseorder.controller.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.controller.aop.AuthenticateAspect;
import com.flab.yousinsa.user.controller.config.SpringTestAopConfig;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith(SpringExtension.class)
@WebMvcTest(PurchaseController.class)
@ContextConfiguration(classes = SpringTestAopConfig.class)
@MockBean(JpaMetamodelMappingContext.class)
class PurchaseControllerTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	PurchaseOrderService purchaseOrderService;

	AuthUser buyer;
	AuthUser notBuyer;

	CreatePurchaseOrderRequestDto purchaseOrderRequestDto;
	CreatePurchaseOrderRequestDto underPurchaseOrderRequestDto;

	@BeforeEach
	public void setUp() {
		buyer = new AuthUser(1L, "buyer", "buyer@yousinsa.com", UserRole.BUYER);
		notBuyer = new AuthUser(2L, "notBuyer", "notBuyer@yousinsa.com", UserRole.STORE_OWNER);

		purchaseOrderRequestDto = new CreatePurchaseOrderRequestDto(1L, 10);
		underPurchaseOrderRequestDto = new CreatePurchaseOrderRequestDto(2L, 0);
	}

	@UnitTest
	@Test
	@DisplayName("1개 미만의 상품 구매에 대한 요청의 경우 실패")
	public void createPurchaseOrderUnderSinglePurchaseAmount() throws Exception {
		// given
		MockHttpSession httpSession = new MockHttpSession();
		httpSession.setAttribute(AuthenticateAspect.AUTH_USER, buyer);

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/orders").session(httpSession)
				.content(objectMapper.writeValueAsString(underPurchaseOrderRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		// 해당 응답은 @Valid 조건으로 Controller에서 발생
		resultActions.andExpect(status().isBadRequest());

		then(purchaseOrderService).should(never())
			.createPurchaseOrder(
				any(CreatePurchaseOrderRequestDto.class),
				any(AuthUser.class)
			);
	}

	@UnitTest
	@Test
	@DisplayName("구매 주문 요청은 Buyer Role을 가진 User만 수행가능")
	public void createPurchaseOrderNotBuyer() throws Exception {
		// given
		MockHttpSession httpSession = new MockHttpSession();
		httpSession.setAttribute(AuthenticateAspect.AUTH_USER, notBuyer);

		// when
		ResultActions resultActions = mockMvc.perform(
			post("/api/v1/orders").session(httpSession)
				.content(objectMapper.writeValueAsString(purchaseOrderRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		// 해당 응답은 @RolePermission 조건으로 Controller에서 발생
		resultActions.andExpect(status().isForbidden());

		then(purchaseOrderService).should(never())
			.createPurchaseOrder(
				any(CreatePurchaseOrderRequestDto.class),
				any(AuthUser.class)
			);
	}
}
