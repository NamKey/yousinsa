package com.flab.yousinsa.user.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.repository.contract.UserRepository;
import com.flab.yousinsa.user.service.contract.UserReadService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserReadServiceImpl implements UserReadService {

	private final UserRepository userRepository;

	@Transactional(readOnly = true)
	@Override
	public UserEntity getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> {
			throw new NotFoundException("requested user id does not exist");
		});
	}
}
