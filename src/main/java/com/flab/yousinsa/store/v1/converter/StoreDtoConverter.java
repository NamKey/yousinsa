package com.flab.yousinsa.store.v1.converter;

import org.springframework.stereotype.Component;

import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.store.v1.dtos.StoreDto;
import com.flab.yousinsa.user.domain.entities.UserEntity;

@Component
public class StoreDtoConverter {

	public Store convertOwnerRequestToEntity(StoreDto.Post request, UserEntity user) {
		Store newStore = Store.builder()
			.storeName(request.getStoreName())
			.storeStatus(StoreStatus.REQUESTED)
			.build();

		newStore.addStoreOwner(user);
		return newStore;
	}
}
