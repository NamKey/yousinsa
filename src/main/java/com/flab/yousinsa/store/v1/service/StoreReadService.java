package com.flab.yousinsa.store.v1.service;

import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

public interface StoreReadService {
	Store getStoreByOwner(Long storeId, AuthUser user);
}
