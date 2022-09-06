package com.flab.yousinsa.user.service.contract;

import com.flab.yousinsa.user.domain.entities.UserEntity;

public interface UserReadService {
	UserEntity getUser(Long userId);
}
