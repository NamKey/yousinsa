package com.flab.yousinsa.product.service.component;

import java.time.Instant;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class ProductStockManageTemplate {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisScript<Long> manageWithCacheScript;

	public Long manageStockWithCache(String key, Long purchaseOrderId, Integer currentStock, Integer purchaseAmount, long ttl) {
		return redisTemplate.execute(
			manageWithCacheScript,
			List.of(key),
			purchaseOrderId, currentStock, purchaseAmount, Instant.now().getEpochSecond(), ttl
		);
	}
}
