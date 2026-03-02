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
public class EmbeddableCredentials {
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "activation_code")
    private String activationCode;

    @Column(name = "activation_date")
    private Instant activationDate;

    @Column(name = "reset_code")
    private String resetCode;

    @Column(name = "reset_date")
    private Instant resetDate;
}
