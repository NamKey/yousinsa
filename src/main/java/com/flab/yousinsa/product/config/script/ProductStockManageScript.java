package com.flab.yousinsa.product.config.script;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class ProductStockManageScript {

	@Bean
	public RedisScript<Long> manageWithCacheScript() {
		ResourceScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("scripts/manageStockWithCache.lua"));
		return RedisScript.of(scriptSource.getResource(), Long.class);
	}
}
