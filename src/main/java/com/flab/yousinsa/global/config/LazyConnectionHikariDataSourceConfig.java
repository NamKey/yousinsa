package com.flab.yousinsa.global.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.util.StringUtils;

import com.zaxxer.hikari.HikariDataSource;

/**
 * https://github.com/spring-projects/spring-boot/issues/15480
 */
@Configuration
public class LazyConnectionHikariDataSourceConfig {
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	public HikariDataSource hikariDataSource(DataSourceProperties properties) {
		HikariDataSource hikariDataSource = createDataSource(properties, HikariDataSource.class);
		if (StringUtils.hasText(properties.getName())) {
			hikariDataSource.setPoolName(properties.getName());
		}
		return hikariDataSource;
	}

	protected static HikariDataSource createDataSource(
		DataSourceProperties properties,
		Class<? extends DataSource> type) {

		return (HikariDataSource)(properties.initializeDataSourceBuilder().type(type).build());
	}

	@Primary
	@Bean
	public DataSource dataSource(HikariDataSource hikariDataSource) {
		// Wrap hikariDataSource in a LazyConnectionDataSourceProxy
		LazyConnectionDataSourceProxy lazyDataSource = new LazyConnectionDataSourceProxy();
		lazyDataSource.setTargetDataSource(hikariDataSource);
		return lazyDataSource;
	}
}
