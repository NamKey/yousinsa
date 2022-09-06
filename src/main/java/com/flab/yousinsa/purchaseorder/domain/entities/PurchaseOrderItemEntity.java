package com.flab.yousinsa.purchaseorder.domain.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Min;

import com.flab.yousinsa.global.common.BaseTimeEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "purchase_order_items")
public class PurchaseOrderItemEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@ManyToOne(optional = false)
	@JoinColumn(name = "purchase_order_id")
	private PurchaseOrderEntity purchaseOrder;

	@ManyToOne(optional = false)
	@JoinColumn(name = "product_option_id")
	private ProductOptionEntity productOption;

	@Min(value = 1)
	private Integer purchaseOrderAmount;
}
