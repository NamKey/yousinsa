package com.flab.yousinsa.admin.service.contract;

import com.flab.yousinsa.admin.domain.dtos.RequestStoreDtoResponse;
import com.flab.yousinsa.store.enums.StoreStatus;

public interface AdminStoreRequestService {
	RequestStoreDtoResponse acceptStoreRequest(Long storeId, StoreStatus requestedStoreStatus);
}
