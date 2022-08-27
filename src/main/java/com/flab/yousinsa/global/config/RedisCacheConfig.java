package com.flab.yousinsa.global.config;

import java.util.HashMap;
import java.util.Map;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class RedisCacheConfig {

	@Value("${spring.redis.host}")
	String REDIS_HOST;

	@Value("${spring.redis.port}")
	String REDIS_PORT;

	@Bean(destroyMethod = "shutdown")
	public RedissonClient redissonClient() {
		Config redisConfig = new Config();
		redisConfig.useSingleServer()
			.setAddress("redis://" + REDIS_HOST + ":" + REDIS_PORT);
		return Redisson.create(redisConfig);
	}

	@Bean
	public RedissonConnectionFactory redisConnectionFactory(RedissonClient redissonClient) {
		return new RedissonConnectionFactory(redissonClient);
	}

	@Bean
	public CacheManager cacheManager(RedissonClient redissonClient) {
		Map<String, CacheConfig> config = new HashMap<>();

		// create "caching" spring cache with ttl = 1 minutes and maxIdleTime = 12 minutes
		config.put("caching", new CacheConfig(1 * 60 * 1000, 12 * 60 * 1000));
		return new RedissonSpringCacheManager(redissonClient, config);
	}
}
