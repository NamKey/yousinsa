package com.flab.yousinsa.product.service.impl;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;
import com.flab.yousinsa.product.service.contract.ProductOptionStockService;
import com.flab.yousinsa.product.service.exception.OutOfStockException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductOptionStockServiceImpl implements ProductOptionStockService {

	private final static String PRODUCT_STOCK_NAMESPACE = "product:option:";
	private final RedisTemplate<String, String> redisTemplate;

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
	public Integer tryDeductProductStock(ProductOptionEntity productOption, Integer purchaseAmount) {
		String key = PRODUCT_STOCK_NAMESPACE + productOption.getId();
		BoundListOperations<String, String> listOps = redisTemplate.boundListOps(key);

		Long size = listOps.size();
		if (size != null && size <= 0) {
			Integer recentRemainedStock = productOption.getProductCount();

			validateRemainedStock(recentRemainedStock, purchaseAmount);

			int currentRemainedStock = recentRemainedStock - purchaseAmount;
			listOps.leftPush(Integer.toString(currentRemainedStock));

			return currentRemainedStock;
		}

		final Deque<Integer> remainedStock = new ArrayDeque<>(1);
		try {
			redisTemplate.execute(new SessionCallback<>() {
				public List<Object> execute(RedisOperations operations) throws DataAccessException {
					redisTemplate.multi();

					Integer recentRemainedStock = getRemainedStock(listOps, 0);

					validateRemainedStock(recentRemainedStock, purchaseAmount);

					int currentRemainedStock = recentRemainedStock - purchaseAmount;

					listOps.leftPushIfPresent(String.valueOf(currentRemainedStock));
					remainedStock.addFirst(currentRemainedStock);
					return new ArrayList<>(); // Synchronize With TransactionManager
				}
			});
		} catch (OutOfStockException ose) {
			remainedStock.removeFirst();
			redisTemplate.discard();
		}

		log.debug("transaction sync with PlatformTransactionManager");

		return remainedStock.getFirst();
	}

	private Integer getRemainedStock(BoundListOperations<String, String> listOperations, long index) {
		String recentStockStr = listOperations.index(index);
		if (recentStockStr == null) {
			throw new OutOfStockException("Out of stock");
		}

		return Integer.valueOf(recentStockStr);
	}

	private void validateRemainedStock(Integer recentRemainedStock, Integer purchaseAmount) {
		if (recentRemainedStock < purchaseAmount) {
			log.debug("{} : Redis stock is out of stock :: remainedStock: {} :: purchaseAmount: {}",
				ProductOptionStockServiceImpl.class.getName(), recentRemainedStock, purchaseAmount);
			throw new OutOfStockException("out of stock");
		}
	}
}
