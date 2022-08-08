package com.flab.yousinsa.purchaseorder.repository.contract;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderEntity;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrderEntity, Long> {
}
