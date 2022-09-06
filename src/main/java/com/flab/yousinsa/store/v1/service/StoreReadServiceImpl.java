package com.flab.yousinsa.store.v1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.domain.StoreRepository;
import com.flab.yousinsa.store.exceptions.IllegalStoreAccessException;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class StoreReadServiceImpl implements StoreReadService {

	private final StoreRepository storeRepository;

	@Transactional(readOnly = true)
	@Override
	public Store getStoreByOwner(Long storeId, AuthUser user) {
		Store foundStore = storeRepository.findByIdWithOwner(storeId)
			.orElseThrow(() -> new NotFoundException("requested store id does not exist"));

		if (!foundStore.getStoreOwner().getId().equals(user.getId())) {
			throw new IllegalStoreAccessException("this store feature only for owner");
		}

		return foundStore;
	}
}
