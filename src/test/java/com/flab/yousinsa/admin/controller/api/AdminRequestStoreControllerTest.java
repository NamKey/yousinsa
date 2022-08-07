package com.flab.yousinsa.admin.controller.api;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
import org.springframework.restdocs.RestDocumentationExtension;
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
import com.flab.yousinsa.user.service.exception.AuthenticationException;
import com.flab.yousinsa.user.service.exception.AuthorizationException;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest({AdminRequestStoreController.class})
@ContextConfiguration(classes = SpringTestAopConfig.class)
@AutoConfigureRestDocs
@MockBean(JpaMetamodelMappingContext.class)
class AdminRequestStoreControllerTest {

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
	@DisplayName("[Admin]Admin Role이 없는 User는 입점 신청 동의를 진행할 수 없음")
	public void acceptStoreRequestOnlyByAdmin() throws Exception {
		// given
		Long storeId = 1L;
		RequestStoreDtoResponse requestStoreDtoResponse = new RequestStoreDtoResponse(storeId, StoreStatus.ACCEPTED);

		MockHttpSession mockHttpSession = new MockHttpSession();
		mockHttpSession.setAttribute(AuthenticateAspect.AUTH_USER, notAdmin);

		given(adminStoreRequestService.acceptStoreRequest(anyLong(), any(StoreStatus.class)))
			.willReturn(requestStoreDtoResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			patch("/api/admin/v1/stores/{storeId}", storeId).session(mockHttpSession)
				.content(objectMapper.writeValueAsString(acceptStoreRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isForbidden())
			.andExpect((result) -> Assertions.assertThat(result.getResolvedException())
				.isInstanceOf(AuthorizationException.class)
				.hasMessageContaining("Requested handler need designated roles")
			);

		then(adminStoreRequestService).should(never()).acceptStoreRequest(anyLong(), any(StoreStatus.class));
	}

	@UnitTest
	@Test
	@DisplayName("[Admin]로그인하지 않은 User는 입점 신청 동의를 진행할 수 없음")
	public void acceptStoreRequestNoLogin() throws Exception {
		// given
		Long storeId = 1L;
		RequestStoreDtoResponse requestStoreDtoResponse = new RequestStoreDtoResponse(storeId, StoreStatus.ACCEPTED);

		given(adminStoreRequestService.acceptStoreRequest(anyLong(), any(StoreStatus.class)))
			.willReturn(requestStoreDtoResponse);

		// when
		ResultActions resultActions = mockMvc.perform(
			patch("/api/admin/v1/stores/{storeId}", storeId)
				.content(objectMapper.writeValueAsString(acceptStoreRequestDto))
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
		);

		// then
		resultActions.andExpect(status().isUnauthorized())
			.andExpect((result) -> Assertions.assertThat(result.getResolvedException())
				.isInstanceOf(AuthenticationException.class)
				.hasMessageContaining("Need to login for using this service")
			);

		then(adminStoreRequestService).should(never()).acceptStoreRequest(anyLong(), any(StoreStatus.class));
	}
}
