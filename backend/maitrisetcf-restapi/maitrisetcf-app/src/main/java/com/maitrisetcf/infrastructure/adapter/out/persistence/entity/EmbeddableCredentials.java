package com.maitrisetcf.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.Instant;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
/** Embeddable JPA component for user authentication credentials. */
public class EmbeddableCredentials {
    @Column(name = "email", nullable = false, unique = true)
    /** Unique email address. */
    private String email;

    @Column(name = "password_hash")
    /** BCrypt password hash. */
    private String passwordHash;

    @Column(name = "activation_code")
    /** One-time account activation code. */
    private String activationCode;

    @Column(name = "activation_date")
    /** Timestamp when the account was activated. */
    private Instant activationDate;

    @Column(name = "reset_code")
    /** One-time password-reset code. */
    private String resetCode;

    @Column(name = "reset_date")
    /** Timestamp of the last password-reset request. */
    private Instant resetDate;
}
