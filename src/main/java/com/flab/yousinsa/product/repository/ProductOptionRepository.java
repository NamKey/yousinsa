package com.flab.yousinsa.product.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOptionEntity, Long> {

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT po FROM ProductOptionEntity po WHERE po.id = :productOptionId")
	Optional<ProductOptionEntity> findByIdWithLock(@Param("productOptionId") Long productOptionId);
}
