package com.flab.yousinsa.product.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.flab.yousinsa.product.domain.dtos.ProductCreateRequestDto;
import com.flab.yousinsa.product.service.contract.ProductCreateService;
import com.flab.yousinsa.user.controller.annotation.AuthSession;
import com.flab.yousinsa.user.controller.annotation.RolePermission;
import com.flab.yousinsa.user.controller.annotation.SignInUser;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.enums.UserRole;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@RestController
public class ProductController {

	private final ProductCreateService productCreateService;

	@AuthSession
	@RolePermission(permittedRoles = {UserRole.STORE_OWNER})
	@PostMapping("api/v1/products")
	public ResponseEntity<Void> registerProduct(
		@RequestBody ProductCreateRequestDto productCreateRequestDto,
		@SignInUser AuthUser user
	) {
		Assert.notNull(productCreateRequestDto, "productCreateRequestDto must not be null");

		Long createdProductId = productCreateService.createProduct(productCreateRequestDto, user);
		String createdResourceUri = "api/v1/products" + createdProductId;

		return ResponseEntity.created(URI.create(createdResourceUri)).build();
	}
}
