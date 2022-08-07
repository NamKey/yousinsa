package com.flab.yousinsa.admin.controller.api;

import static com.flab.yousinsa.ApiDocumentUtils.*;
import static org.assertj.core.api.InstanceOfAssertFactories.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.*;
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
import com.flab.yousinsa.admin.domain.dtos.AcceptStoreRequestDto;
import com.flab.yousinsa.admin.domain.dtos.RequestStoreDtoResponse;
import com.flab.yousinsa.admin.service.contract.AdminStoreRequestService;
import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.user.controller.aop.AuthenticateAspect;
import com.flab.yousinsa.user.controller.config.SpringTestAopConfig;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest({AdminRequestStoreController.class})
@ContextConfiguration(classes = SpringTestAopConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
class AdminRequestStoreControllerDocTest {

	@Autowired
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@MockBean
	AdminStoreRequestService adminStoreRequestService;

	AuthUser admin;
	AuthUser notAdmin;
	AcceptStoreRequestDto acceptStoreRequestDto;
	@BeforeEach
	public void setUp() {
		admin = new AuthUser(1L, "admin", "admin@yousinsa.com", UserRole.ADMIN);
		notAdmin = new AuthUser(2L, "notAdmin", "notAdmin@yousinsa.com", UserRole.STORE_OWNER);
		acceptStoreRequestDto = new AcceptStoreRequestDto(StoreStatus.ACCEPTED);
	}

	@UnitTest
	@Test
	@DisplayName("[Admin]상점 신청 Accept API Doc")
	public void acceptAdminStoreRequest() throws Exception {
		// given
		Long storeId = 1L;
		RequestStoreDtoResponse requestStoreDtoResponse = new RequestStoreDtoResponse(storeId, StoreStatus.ACCEPTED);

		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(AuthenticateAspect.AUTH_USER, admin);

		given(adminStoreRequestService.acceptStoreRequest(anyLong(), any(StoreStatus.class)))
			.willReturn(requestStoreDtoResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			RestDocumentationRequestBuilders.patch("/api/admin/v1/stores/{storeId}", storeId)
				.session(mockHttpSession)
				.content(objectMapper.writeValueAsString(acceptStoreRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isAccepted())
			.andExpect(content().string(objectMapper.writeValueAsString(requestStoreDtoResponse)))
			.andDo(
				document("admin-store-accept",
					getDocumentRequest(),
					getDocumentResponse(),
					pathParameters(
						parameterWithName("storeId").description("입점 동의할 Store Id")
							.attributes(key("type").value(LONG))
					),
					requestFields(
						fieldWithPath("requestedStoreStatus").type(JsonFieldType.STRING)
							.description("변경하고자 하는 Store 상태")
					),
					responseFields(
						fieldWithPath("storeId").type(JsonFieldType.NUMBER).description("Accept가 진행된 Store ID"),
						fieldWithPath("storeStatus").type(JsonFieldType.STRING).description("Accepted Store의 상태")
					)
				)
			);

		then(adminStoreRequestService).should()
			.acceptStoreRequest(refEq(storeId), eq(acceptStoreRequestDto.getRequestedStoreStatus()));
	}
}
