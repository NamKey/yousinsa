package com.flab.yousinsa.store.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.flab.yousinsa.global.common.BaseTimeEntity;
import com.flab.yousinsa.store.enums.StoreStatus;
import com.flab.yousinsa.user.domain.entities.UserEntity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "stores")
public class Store extends BaseTimeEntity {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long id;

	@NotNull
	@Column(name = "store_name")
	private String storeName;

	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private final List<UserEntity> storeOwners = new ArrayList<>();

	@Setter
	@Enumerated(value = EnumType.STRING)
	@Column(name = "store_status")
	private StoreStatus storeStatus;

	public void addStoreOwner(UserEntity owner) {
		owner.setStore(this);
		storeOwners.add(owner);
	}
}
