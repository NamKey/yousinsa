package com.flab.yousinsa.store.v1.service;

import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.store.domain.StoreRepository;
import com.flab.yousinsa.store.exceptions.NotValidStoreException;
import com.flab.yousinsa.store.v1.converter.StoreDtoConverter;
import com.flab.yousinsa.store.v1.dtos.StoreDto;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.service.contract.UserReadService;

import lombok.RequiredArgsConstructor;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Primary
@RequiredArgsConstructor
@Service
public class StoreAssembleServiceImpl implements StoreService {

	private final StoreDtoConverter storeDtoConverter;
	private final StoreRepository storeRepository;

	private final UserReadService userReadService;

	@Transactional
	@Override
	public Long createStore(StoreDto.Post request, AuthUser user) {
		UserEntity signInUser = userReadService.getUser(user.getId());
		validateStoreOwnerByUserId(signInUser);

		Store store = storeDtoConverter.convertOwnerRequestToEntity(request, signInUser);
		Store createdStore = storeRepository.save(store);
		return createdStore.getId();
	}

	private void validateStoreOwnerByUserId(UserEntity user) {
		boolean isPresent = storeRepository.existsByStoreOwners(user);
		if (isPresent) {
			throw new NotValidStoreException("Already exists.");
		}
	}
}
