package com.flab.yousinsa.purchaseorder.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderEntity;
import com.flab.yousinsa.purchaseorder.domain.enums.PurchaseOrderStatus;
import com.flab.yousinsa.purchaseorder.repository.contract.PurchaseOrderRepository;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderUpdateService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PurchaseOrderUpdateServiceImpl implements PurchaseOrderUpdateService {

	private final PurchaseOrderRepository purchaseOrderRepository;

	@Transactional
	@Override
	public Long acceptPurchaseOrderStatus(Long purchaseOrderId) {
		PurchaseOrderEntity foundPurchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
			.orElseThrow(() -> new NotFoundException("not found purchaseOrder by id"));
		foundPurchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatus.ACCEPTED);
		return foundPurchaseOrder.getId();
	}
}
