package com.flab.yousinsa.admin.domain.dtos;

import com.flab.yousinsa.store.enums.StoreStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AcceptStoreRequestDto {
	private StoreStatus requestedStoreStatus;
}
