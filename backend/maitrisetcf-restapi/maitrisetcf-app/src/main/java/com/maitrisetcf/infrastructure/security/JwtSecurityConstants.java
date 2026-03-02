package com.maitrisetcf.infrastructure.security;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JwtSecurityConstants {
    public static final MacAlgorithm JWT_ALGORITHM = MacAlgorithm.HS256;
    public static final String AUTHORITIES_KEY = "auth";
}
