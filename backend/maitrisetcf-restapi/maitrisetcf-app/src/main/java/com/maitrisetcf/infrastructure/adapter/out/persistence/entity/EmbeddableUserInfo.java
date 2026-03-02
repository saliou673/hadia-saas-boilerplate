package com.maitrisetcf.infrastructure.adapter.out.persistence.entity;

import com.maitrisetcf.domain.enumerations.UserGender;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class EmbeddableUserInfo {
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private UserGender gender;

    @Column(name = "address")
    private String address;

    @Column(name = "language_key")
    private String languageKey;

    @Column(name = "image_url")
    private String imageUrl;
}
