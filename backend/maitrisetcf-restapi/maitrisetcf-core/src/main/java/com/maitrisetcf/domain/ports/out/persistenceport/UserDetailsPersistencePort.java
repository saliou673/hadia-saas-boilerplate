package com.maitrisetcf.domain.ports.out.persistenceport;

import com.maitrisetcf.domain.models.user.User;

import java.util.Optional;

public interface UserDetailsPersistencePort {
    Optional<User> findUserWithAuthoritiesByEmail(String email);
}
