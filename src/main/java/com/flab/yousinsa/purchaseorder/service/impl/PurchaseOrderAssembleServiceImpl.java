package com.flab.yousinsa.purchaseorder.service.impl;

import java.time.LocalDateTime;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.flab.yousinsa.global.exceptions.NotFoundException;
import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.domain.events.SellProductEvent;
import com.flab.yousinsa.product.service.component.SellProductEventPublisher;
import com.flab.yousinsa.product.service.contract.ProductOptionReadService;
import com.flab.yousinsa.product.service.contract.ProductOptionUpdateService;
import com.flab.yousinsa.product.service.exception.OutOfStockException;
import com.flab.yousinsa.purchaseorder.domain.dtos.CreatePurchaseOrderRequestDto;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderEntity;
import com.flab.yousinsa.purchaseorder.domain.entities.PurchaseOrderItemEntity;
import com.flab.yousinsa.purchaseorder.domain.enums.PurchaseOrderStatus;
import com.flab.yousinsa.purchaseorder.repository.contract.PurchaseOrderRepository;
import com.flab.yousinsa.purchaseorder.service.contract.PurchaseOrderService;
import com.flab.yousinsa.user.domain.dtos.AuthUser;
import com.flab.yousinsa.user.domain.entities.UserEntity;
import com.flab.yousinsa.user.service.contract.UserReadService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class PurchaseOrderAssembleServiceImpl implements PurchaseOrderService {

	private final ProductOptionUpdateService productOptionUpdateService;
	private final ProductOptionReadService productOptionReadService;
	private final UserReadService userReadService;
	private final static String PRODUCT_STOCK_NAMESPACE = "product:option:";
	private final PurchaseOrderRepository purchaseOrderRepository;
	private final SellProductEventPublisher sellProductEventPublisher;
	private final RedisTemplate<String, String> redisTemplate;

	@Transactional
	@Override
	public Long createPurchaseOrder(CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto, AuthUser user) {
		Assert.notNull(createPurchaseOrderRequestDto, "purchaseOrder request must not be null");

		productOptionUpdateService.sellProduct(
			createPurchaseOrderRequestDto.getProductOptionId(),
			createPurchaseOrderRequestDto.getPurchaseOrderAmount()
		);

		UserEntity buyer = userReadService.getUser(user.getId());

		PurchaseOrderEntity purchaseOrder = PurchaseOrderEntity.builder()
			.buyer(buyer)
			.purchaseOrderStatus(PurchaseOrderStatus.ACCEPTED)
			.build();

		PurchaseOrderItemEntity purchaseOrderItem = PurchaseOrderItemEntity.builder()
			.purchaseOrder(purchaseOrder)
			.purchaseOrderAmount(createPurchaseOrderRequestDto.getPurchaseOrderAmount())
			.productOption(
				productOptionReadService.getProductOption(createPurchaseOrderRequestDto.getProductOptionId())
			)
			.build();

		purchaseOrder.addPurchaseOrderItem(purchaseOrderItem);

		PurchaseOrderEntity acceptedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

		return acceptedPurchaseOrder.getId();
	}

	/**
	 * 1. Redis 확인하여 구매 요청된 ProductOption 갯수와 ProductOption 재고 조회
	 * 2-1. 없다면 -> 3
	 * 2-2. 있다면 -> 5
	 * 3. DB에서 재고 조회
	 * -- Redis Transaction 시작 --
	 * 4. Redis에 현재 재고 기록 || [key, value] [productoption:{option_id}, productCount]
	 * 5. 재고와 요청 수량 비교 검증
	 * 6. 현재 재고(RPOP) - 현재 요청된 구매 = 남아있는 재고 -> Redis 현재 재고 기록(LPUSH)
	 * -- Redis Transaction 종료
	 *
	 * 1. EventPublisher를 통해서 SellProductEvent 발행
	 * 2. SellProductEventListener는 TransactionalListener이므로 Trasaction이 종료 후 Event를 Consume함
	 * 3. Listerner에서는 SellProductEvent의 productOptionId, purchaseAmount를 활용하여 Update 수행
	 *
	 * -- Key 정보
	 * - 현재 구매중인 Option ID
	 * -- Value 정보
	 * - 현재 재고 정보
	 */
	@Transactional
	@Override
	public Long createPurchaseOrderWithRedis(
		CreatePurchaseOrderRequestDto createPurchaseOrderRequestDto,
		AuthUser user
	) {
		Assert.notNull(createPurchaseOrderRequestDto, "purchaseOrder request must not be null");
		Long productOptionId = createPurchaseOrderRequestDto.getProductOptionId();
		Integer purchaseOrderAmount = createPurchaseOrderRequestDto.getPurchaseOrderAmount();

		Integer remainedStock = tryDeductProductStock(productOptionId, purchaseOrderAmount);

		UserEntity buyer = userReadService.getUser(user.getId());

		PurchaseOrderEntity purchaseOrder = PurchaseOrderEntity.builder()
			.buyer(buyer)
			.purchaseOrderStatus(PurchaseOrderStatus.IN_PROGRESS)
			.build();

		PurchaseOrderItemEntity purchaseOrderItem = PurchaseOrderItemEntity.builder()
			.purchaseOrder(purchaseOrder)
			.purchaseOrderAmount(purchaseOrderAmount)
			.productOption(productOptionReadService.getProductOption(
				productOptionId
			))
			.build();

		purchaseOrder.addPurchaseOrderItem(purchaseOrderItem);

		PurchaseOrderEntity acceptedPurchaseOrder = purchaseOrderRepository.save(purchaseOrder);

		sellProductEventPublisher.publishSellProductEvent(new SellProductEvent(
			productOptionId,
			purchaseOrderAmount,
			acceptedPurchaseOrder.getId(),
			remainedStock,
			LocalDateTime.now()
		));

		return acceptedPurchaseOrder.getId();
	}

	@Transactional
	@Override
	public Long acceptPurchaseOrderStatus(Long purchaseOrderId) {
		PurchaseOrderEntity foundPurchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
			.orElseThrow(() -> new NotFoundException("not found purchaseOrder by id"));
		foundPurchaseOrder.setPurchaseOrderStatus(PurchaseOrderStatus.ACCEPTED);
		return foundPurchaseOrder.getId();
	}

	private Integer tryDeductProductStock(Long productOptionId, Integer purchaseAmount) {
		String key = PRODUCT_STOCK_NAMESPACE + productOptionId;
		final Integer[] remainedStock = new Integer[1];

		BoundListOperations<String, String> listOps = redisTemplate.boundListOps(key);

		Long size = listOps.size();
		if (size != null && size <= 0) {
			ProductOptionEntity productOption = productOptionReadService.getProductOption(productOptionId);
			Integer recentRemainedStock = productOption.getProductCount();

			if (recentRemainedStock < purchaseAmount) {
				throw new OutOfStockException("out of stock");
			}

			listOps.leftPush(Integer.toString(recentRemainedStock - purchaseAmount));
			remainedStock[0] = recentRemainedStock - purchaseAmount;
		} else {
			redisTemplate.execute(new SessionCallback<>() {
				public Object execute(RedisOperations operations) throws DataAccessException {
					operations.multi();
					BoundListOperations<String, String> txListOps = operations.boundListOps(key);

					String recentStockStr = txListOps.index(0);
					Integer recentRemainedStock = Integer.valueOf(recentStockStr != null ? recentStockStr : "0");

					if (recentRemainedStock < purchaseAmount) {
						throw new OutOfStockException("out of stock");
					}

					txListOps.leftPushIfPresent(String.valueOf(recentRemainedStock - purchaseAmount));
					txListOps.rightPop();

					remainedStock[0] = recentRemainedStock - purchaseAmount;
					return operations.exec();
				}
			});
		}

		return remainedStock[0];
	}
}
