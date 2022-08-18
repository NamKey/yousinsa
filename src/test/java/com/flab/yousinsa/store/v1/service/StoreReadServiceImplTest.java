package com.flab.yousinsa.store.v1.service;

import static org.mockito.BDDMockito.*;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.domain.StoreRepository;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.store.exceptions.IllegalStoreAccessException;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;

@ExtendWith(MockitoExtension.class)
class StoreReadServiceImplTest {

	@Mock
	StoreRepository storeRepository;

	@InjectMocks
	StoreReadServiceImpl storeReadService;

	AuthUser ownerAuth;
	AuthUser notOwnerAuth;
	UserEntity owner;
	Store store;

	@BeforeEach
	public void setUp() {
		ownerAuth = new AuthUser(1L, "test", "test@yousinsa.com", UserRole.STORE_OWNER);
		notOwnerAuth = new AuthUser(2L, "notOwner", "notOwner@yousinsa.com", UserRole.STORE_OWNER);

		owner = UserEntity.builder()
			.id(1L)
			.userName("test")
			.userEmail("test@yousinsa.com")
			.userRole(UserRole.STORE_OWNER)
			.build();

		store = Store.builder()
			.id(1L)
			.storeName("testStore")
			.storeStatus(StoreStatus.ACCEPTED)
			.build();

		store.addStoreOwner(owner);
	}

	@UnitTest
	@Test
	@DisplayName("로그인 된 사용자가 해당 Store에 Owner가 아니면 Store Entity를 갖고 올 수 없음")
	public void getStoreWithLoginUserIsNotOwnerOfStore() {
		// given
		Long storeId = 1L;

		given(storeRepository.findByIdWithOwner(anyLong())).willReturn(Optional.ofNullable(store));

		// when
		Assertions.assertThatThrownBy(() -> {
				storeReadService.getStoreByOwner(storeId, notOwnerAuth);
			})
			.isInstanceOf(IllegalStoreAccessException.class)
			.hasMessageContaining("this store feature only for owner");

		// then
		then(storeRepository).should().findByIdWithOwner(eq(storeId));
	}
}
