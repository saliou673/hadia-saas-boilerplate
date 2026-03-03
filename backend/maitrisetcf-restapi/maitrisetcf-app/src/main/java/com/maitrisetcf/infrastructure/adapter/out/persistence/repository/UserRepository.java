package com.maitrisetcf.infrastructure.adapter.out.persistence.repository;

import com.maitrisetcf.domain.enumerations.UserStatus;
import com.maitrisetcf.infrastructure.adapter.out.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;

import java.time.Instant;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link UserEntity}.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findOneWithAuthoritiesByUserCredentialsEmailIgnoreCase(String email);

    Optional<UserEntity> findOneWithAuthoritiesById(Long id);

    Optional<UserEntity> findOneByUserCredentialsActivationCode(String activationCode);

    Optional<UserEntity> findOneByUserCredentialsResetCode(String resetCode);

    Optional<UserEntity> findOneByUserCredentialsEmailIgnoreCase(String email);

    boolean existsByUserCredentialsActivationCode(String activationCode);

    boolean existsByUserCredentialsResetCode(String resetCode);

    @Modifying
    int deleteAllByUserCredentialsActivationCodeIsNotNullAndStatusIsNotAndCreationDateBefore(UserStatus status, Instant dateTime);

    @Modifying
    int deleteAllByStatusAndLastUpdateDateBefore(UserStatus status, Instant dateTime);
}
