package com.maitrisetcf.infrastructure.security;

import com.maitrisetcf.domain.exceptions.AccountNotActivatedException;
import com.maitrisetcf.domain.exceptions.AccountNotFoundException;
import com.maitrisetcf.domain.exceptions.EmailInvalidException;
import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.ports.out.persistenceport.UserDetailsPersistencePort;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
@RequiredArgsConstructor
public class DomainUserDetailsService implements UserDetailsService {

    private final UserDetailsPersistencePort userDetailsPersistencePort;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(@Nonnull final String email) {
        if (!(new EmailValidator().isValid(email, null))) {
            throw new EmailInvalidException("Invalid email: " + email);
        }

        return userDetailsPersistencePort
                .findUserWithAuthoritiesByEmail(email)
                .map(user -> createSpringSecurityUser(email, user))
                .orElseThrow(() -> new AccountNotFoundException("User with email " + email + " not found"));
    }

    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String email, User user) {
        if (!user.isActive()) {
            throw new AccountNotActivatedException("User with email " + email + " is not activated");
        }

        List<SimpleGrantedAuthority> grantedAuthorities = user
                .resolvePermissions()
                .stream()
                .map(Permission::code)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new AuthenticatedUser(user.getUserCredentials().getEmail(), user.getUserCredentials().getPasswordHash(), grantedAuthorities, user.getUserInfo().languageKey());
    }
}
