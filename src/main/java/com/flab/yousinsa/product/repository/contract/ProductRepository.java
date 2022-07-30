package com.flab.yousinsa.product.repository.contract;

import org.springframework.data.jpa.repository.JpaRepository;

import com.flab.yousinsa.product.domain.entity.ProductEntity;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

}
