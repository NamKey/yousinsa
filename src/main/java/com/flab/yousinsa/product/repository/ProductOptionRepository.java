package com.flab.yousinsa.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOptionEntity, Long> {
}
