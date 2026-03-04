package com.hadiasaas.infrastructure.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Class representing the authenticated user (for spring security).
 * It's a simplified version of ${@link com.hadiasaas.domain.models.user.User}.
 */
@Getter
public class AuthenticatedUser extends User {
    private final String languageKey;

    public AuthenticatedUser(String username,
                             String password,
                             Collection<? extends GrantedAuthority> authorities,
                             String languageKey) {
        super(username, password, authorities);
        this.languageKey = languageKey;
    }
}
