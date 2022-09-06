package com.flab.yousinsa.purchaseorder.domain.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.flab.yousinsa.global.common.BaseTimeEntity;
import com.flab.yousinsa.purchaseorder.domain.enums.PurchaseOrderStatus;
import com.flab.yousinsa.user.domain.entities.UserEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "purchase_orders")
@Entity
public class PurchaseOrderEntity extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "buyer_id")
	private UserEntity buyer;

	@Enumerated(value = EnumType.STRING)
	private PurchaseOrderStatus purchaseOrderStatus;

	@OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL)
	private final List<PurchaseOrderItemEntity> purchaseOrderItems = new ArrayList<>();

	public void addPurchaseOrderItem(PurchaseOrderItemEntity purchaseOrderItem) {
		purchaseOrderItem.setPurchaseOrder(this);
		purchaseOrderItems.add(purchaseOrderItem);
	}
}
