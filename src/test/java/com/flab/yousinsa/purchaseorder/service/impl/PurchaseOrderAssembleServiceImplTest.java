package com.flab.yousinsa.purchaseorder.service.impl;

import static org.mockito.BDDMockito.*;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.flab.yousinsa.annotation.UnitTest;
import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;
import com.flab.yousinsa.product.service.contract.ProductOptionReadService;
import com.flab.yousinsa.product.service.contract.ProductOptionUpdateService;
import com.flab.yousinsa.product.service.exception.OutOfStockException;
import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderEntity;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderItemEntity;
import com.flab.yousinsa.purchaseorder.domain.enums.PurchaseOrderStatus;
import com.flab.yousinsa.purchaseorder.repository.contract.PurchaseOrderRepository;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.domain.enums.UserRole;
import com.flab.yousinsa.user.service.contract.UserReadService;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderAssembleServiceImplTest {

	@Mock
	ProductOptionReadService productOptionReadService;

	@Mock
	ProductOptionUpdateService productOptionUpdateService;

	@Mock
	UserReadService userReadService;

	@Mock
	PurchaseOrderRepository purchaseOrderRepository;

	@InjectMocks
	PurchaseOrderAssembleServiceImpl purchaseOrderAssembleService;

	AuthUser buyer;

	CreatePurchaseOrderRequestDto smallOptionRequestDto;
	CreatePurchaseOrderRequestDto overCountOptionRequestDto;
	CreatePurchaseOrderRequestDto outOfStockOptionRequestDto;
	CreatePurchaseOrderRequestDto notFoundOptionRequestDto;

	UserEntity buyerEntity;

	ProductEntity niceProduct;
	ProductOptionEntity smallOption;
	ProductOptionEntity overCountStockOption;
	ProductOptionEntity outOfStockOption;

	PurchaseOrderEntity purchaseOrder;

	PurchaseOrderItemEntity smallOptionPurchaseItem;

	@BeforeEach
	public void setUp() {
		buyer = new AuthUser(1L, "buyer", "buyer@yousinsa.com", UserRole.BUYER);

		smallOptionRequestDto = CreatePurchaseOrderRequestDto.builder()
			.productOptionId(1L)
			.purchaseOrderAmount(5)
			.build();

		overCountOptionRequestDto = CreatePurchaseOrderRequestDto.builder()
			.productOptionId(2L)
			.purchaseOrderAmount(80)
			.build();

		outOfStockOptionRequestDto = CreatePurchaseOrderRequestDto.builder()
			.productOptionId(3L)
			.purchaseOrderAmount(10)
			.build();

		notFoundOptionRequestDto = CreatePurchaseOrderRequestDto.builder()
			.productOptionId(99L)
			.purchaseOrderAmount(10)
			.build();

		buyerEntity = UserEntity.builder()
			.id(1L)
			.userEmail("buyer@yousinsa.com")
			.userName("buyer")
			.userRole(UserRole.BUYER)
			.build();

		List<ProductOptionEntity> options = new ArrayList<>();
		niceProduct = ProductEntity.builder()
			.id(1L)
			.productName("niceProduct")
			.category(ProductCategory.TOP)
			.productPrice(1000L)
			.options(options)
			.build();

		smallOption = ProductOptionEntity.builder()
			.id(1L)
			.productCount(10)
			.productSize("small")
			.product(niceProduct)
			.build();

		overCountStockOption = ProductOptionEntity.builder()
			.id(2L)
			.productCount(5)
			.productSize("medium")
			.product(niceProduct)
			.build();

		outOfStockOption = ProductOptionEntity.builder()
			.id(3L)
			.productCount(0)
			.productSize("large")
			.product(niceProduct)
			.build();
		options.add(smallOption);
		options.add(overCountStockOption);
		options.add(outOfStockOption);

		purchaseOrder = PurchaseOrderEntity.builder()
			.id(1L)
			.purchaseOrderStatus(PurchaseOrderStatus.ACCEPTED)
			.buyer(buyerEntity)
			.build();

		smallOptionPurchaseItem = PurchaseOrderItemEntity.builder()
			.id(1L)
			.purchaseOrderAmount(smallOptionRequestDto.getPurchaseOrderAmount())
			.purchaseOrder(purchaseOrder)
			.productOption(smallOption)
			.build();

		purchaseOrder.addPurchaseOrderItem(smallOptionPurchaseItem);

	}

	@UnitTest
	@Test
	@DisplayName("재고 수량보다 많은 제품을 주문하는 경우 실패")
	public void orderRequestProductCountIsOverStockAmount() {
		// given
		given(productOptionUpdateService.sellProduct(anyLong(), anyInt())).willThrow(
			new OutOfStockException("requested purchase amount is over product stock count"));

		// when
		Assertions.assertThatThrownBy(() -> {
				purchaseOrderAssembleService.createPurchaseOrder(overCountOptionRequestDto, buyer);
			})
			.isInstanceOf(OutOfStockException.class)
			.hasMessageContaining("requested purchase amount is over product stock count");

		// then
		then(userReadService).shouldHaveNoInteractions();
		then(productOptionReadService).shouldHaveNoInteractions();
		then(productOptionUpdateService).should().sellProduct(
			eq(overCountOptionRequestDto.getProductOptionId()),
			eq(overCountOptionRequestDto.getPurchaseOrderAmount())
		);
		then(purchaseOrderRepository).should(never()).save(any(PurchaseOrderEntity.class));
	}

	@UnitTest
	@Test
	@DisplayName("재고 수량이 없는 제품 옵션을 주문하는 경우 실패")
	public void orderRequestProductIsOutOfStock() {
		// given
		given(productOptionUpdateService.sellProduct(anyLong(), anyInt())).willThrow(
			new OutOfStockException("requested product is out of stock"));

		// when
		Assertions.assertThatThrownBy(() -> {
				purchaseOrderAssembleService.createPurchaseOrder(outOfStockOptionRequestDto, buyer);
			})
			.isInstanceOf(OutOfStockException.class)
			.hasMessageContaining("requested product is out of stock");

		// then
		then(userReadService).shouldHaveNoInteractions();
		then(productOptionReadService).shouldHaveNoInteractions();
		then(productOptionUpdateService).should().sellProduct(
			eq(outOfStockOptionRequestDto.getProductOptionId()),
			eq(outOfStockOptionRequestDto.getPurchaseOrderAmount())
		);
		then(purchaseOrderRepository).should(never()).save(any(PurchaseOrderEntity.class));
	}

	@UnitTest
	@Test
	@DisplayName("올바르지 않은 상품 옵션에 대해서 주문하는 경우 실패")
	public void orderRequestProductOptionDoesNotExist() {
		// given
		given(userReadService.getUser(anyLong())).willReturn(buyerEntity);
		given(productOptionReadService.getProductOption(anyLong()))
			.willThrow(new NotFoundException("requested product option id does not exist"));

		// when
		Assertions.assertThatThrownBy(() -> {
				purchaseOrderAssembleService.createPurchaseOrder(notFoundOptionRequestDto, buyer);
			})
			.isInstanceOf(NotFoundException.class)
			.hasMessageContaining("requested product option id does not exist");

		// then
		then(userReadService).should().getUser(eq(buyer.getId()));
		then(productOptionReadService).should().getProductOption(eq(notFoundOptionRequestDto.getProductOptionId()));
		then(purchaseOrderRepository).should(never()).save(any(PurchaseOrderEntity.class));
	}

	@UnitTest
	@Test
	@DisplayName("올바른 상품 옵션에 대해서 재고 이하의 수량을 주문하는 경우 주문 접수")
	public void createPurchaseOrderWithProperRequest() {
		// given
		given(userReadService.getUser(anyLong())).willReturn(buyerEntity);
		given(productOptionReadService.getProductOption(anyLong())).willReturn(smallOption);
		given(productOptionUpdateService.sellProduct(anyLong(), anyInt())).willReturn(smallOption.getId());
		given(purchaseOrderRepository.save(any(PurchaseOrderEntity.class))).willReturn(purchaseOrder);

		// when
		Long purchaseOrderId = purchaseOrderAssembleService.createPurchaseOrder(smallOptionRequestDto, buyer);

		// then
		Assertions.assertThat(purchaseOrderId).isEqualTo(purchaseOrder.getId());

		then(userReadService).should().getUser(eq(buyer.getId()));
		then(productOptionUpdateService).should()
			.sellProduct(
				eq(smallOptionRequestDto.getProductOptionId()),
				eq(smallOptionRequestDto.getPurchaseOrderAmount())
			);
		then(productOptionReadService).should().getProductOption(eq(smallOption.getId()));
		then(purchaseOrderRepository).should().save(refEq(purchaseOrder, "purchaseOrderItems", "id"));
	}
}
