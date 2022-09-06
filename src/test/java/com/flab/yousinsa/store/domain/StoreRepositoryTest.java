package com.flab.yousinsa.store.domain;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;
import com.flab.yousinsa.user.repository.contract.UserRepository;

@DataJpaTest
class StoreRepositoryTest {

	@Autowired
	StoreRepository storeRepository;

	@Autowired
	UserRepository userRepository;

	UserEntity user;

	@BeforeEach
	public void setUp() {
		user = UserEntity.builder()
			.userName("test")
			.userEmail("test@test.com")
			.userPassword("test")
			.userRole(UserRole.BUYER)
			.build();
	}

	@Test
	@DisplayName("입점 신청")
	public void createStore() {
		// given
		Store createStore = Store.builder()
			.storeName("store")
			.storeOwner(user)
			.storeStatus(StoreStatus.REQUESTED)
			.build();

		// when
		Store store = storeRepository.save(createStore);

		// then
		Assertions.assertEquals(createStore.getStoreName(), store.getStoreName());
		Assertions.assertEquals(createStore.getStoreOwner().getUserName(), store.getStoreOwner().getUserName());
	}

	@Test
	@DisplayName("Owner와 함께 상점 조회(Fetch Join Test)")
	public void getStoreWithOwner() {
		// given
		userRepository.save(user);
		Store createStore = Store.builder()
			.storeName("store")
			.storeOwner(user)
			.storeStatus(StoreStatus.REQUESTED)
			.build();
		Store savedStore = storeRepository.save(createStore);

		// when
		Optional<Store> requestedStore = storeRepository.findByIdWithOwner(1L);

		// then
		assertThat(requestedStore)
			.hasValue(savedStore)
			.map(Store::getStoreOwner)
			.hasValue(user);

	}

}
