package com.flab.yousinsa.product.service.impl;

import org.springframework.stereotype.Service;

import com.flab.yousinsa.product.service.component.ProductStockManageTemplate;
import com.flab.yousinsa.product.service.contract.ProductOptionStockService;
import com.flab.yousinsa.product.service.exception.OutOfStockException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductOptionStockServiceImpl implements ProductOptionStockService {

	private final static String PRODUCT_STOCK_NAMESPACE = "product:option:";
	private final ProductStockManageTemplate productStockManageTemplate;

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
	 * @return 남은 재고 - 해당 재고를 통해 Consistency 평가 용도
	 */
	@Override
	public Long tryDeductProductStock(
		Long productOptionId, Integer currentStock,
		Integer purchaseAmount, Long purchaseOrderId
	) {
		String key = PRODUCT_STOCK_NAMESPACE + productOptionId;

		Long remainedStock = productStockManageTemplate.manageStockWithCache(
			key,
			purchaseOrderId,
			currentStock,
			purchaseAmount,
			1000
		);

		log.info("[OptionId]::{}[PurchaseAmount]::{}[Remained]::{}", productOptionId, purchaseAmount, remainedStock);
		if (remainedStock == null) {
			throw new OutOfStockException("out of stock");
		}

		return remainedStock;
	}
}
