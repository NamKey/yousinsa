package com.flab.yousinsa.product.repository;

import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.flab.yousinsa.product.domain.entity.ProductOptionEntity;

@Repository
public interface ProductOptionRepository extends JpaRepository<ProductOptionEntity, Long> {

	@Lock(value = LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT po FROM ProductOptionEntity po WHERE po.id = :productOptionId")
	Optional<ProductOptionEntity> findByIdWithLock(@Param("productOptionId") Long productOptionId);

	@Transactional
	@Modifying
	@Query("UPDATE ProductOptionEntity po SET po.productCount=po.productCount - :purchaseAmount WHERE po.id = :productOptionId AND po.productCount - :purchaseAmount > 0")
	void updateProductOptionCount(
		@Param("productOptionId") Long productOptionId,
		@Param("purchaseAmount") Integer purchaseAmount
	);
}
