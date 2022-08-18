package com.flab.yousinsa.store.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.flab.yousinsa.user.domain.entities.UserEntity;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {

	boolean existsByStoreOwners(UserEntity user);
	
	@Query(value = "SELECT DISTINCT s FROM Store s JOIN FETCH s.storeOwners")
	Optional<Store> findByIdWithOwner(Long storeId);
}
