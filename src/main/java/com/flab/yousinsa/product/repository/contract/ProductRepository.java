package com.flab.yousinsa.product.repository.contract;

import org.hibernate.annotations.BatchSize;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.yousinsa.product.domain.entity.ProductEntity;
import com.flab.yousinsa.product.domain.enums.ProductCategory;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

	@BatchSize(size = 100)
	Page<ProductEntity> findAllByCategory(ProductCategory category, Pageable pageable);
}
