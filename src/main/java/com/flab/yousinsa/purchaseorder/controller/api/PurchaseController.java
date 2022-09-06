package com.flab.yousinsa.purchaseorder.controller.api;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.controller.annotation.AuthSession;
import com.flab.yousinsa.user.controller.annotation.RolePermission;
import com.flab.yousinsa.user.controller.annotation.SignInUser;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.enums.UserRole;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PurchaseController {

	private final PurchaseOrderService purchaseOrderService;

	@AuthSession
	@RolePermission(permittedRoles = {UserRole.BUYER})
	@PostMapping("api/v1/orders")
	public ResponseEntity<Void> createPurchaseOrder(
		@Valid @RequestBody CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto,
		@SignInUser AuthUser user
	) {
		Long createdOrderId = purchaseOrderService.createPurchaseOrder(
			createPurchaseOrderRequestDto,
			user
		);

		return ResponseEntity.created(URI.create("api/v1/orders/" + createdOrderId)).build();
	}
}
