package com.maitrisetcf.domain.models.user;


public record AuthenticatedUser(
        String email,
        String authorities
) {
}

