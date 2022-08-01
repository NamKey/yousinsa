package com.flab.yousinsa.user.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.flab.yousinsa.global.common.BaseTimeEntity;
import com.flab.yousinsa.store.domain.Store;
import com.flab.yousinsa.user.domain.enums.UserRole;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class UserEntity extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	private String userName;

	@Setter
	@NotNull
	@Email
	private String userEmail;

	@NotNull
	@Column(name = "user_password")
	private String userPassword;

	@Setter
	@Enumerated(value = EnumType.STRING)
	private UserRole userRole;

	@OneToOne(mappedBy = "storeOwner")
	private Store store;
}
