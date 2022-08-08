package com.flab.yousinsa.purchaseorder.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.repository.contract.PurchaseOrderRepository;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PurchaseOrderAssembleServiceImpl implements PurchaseOrderService {

	private final PurchaseOrderRepository purchaseOrderRepository;

	@Transactional
	@Override
	public Long createPurchaseOrder(CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto, AuthUser user) {
		return null;
	}
}
