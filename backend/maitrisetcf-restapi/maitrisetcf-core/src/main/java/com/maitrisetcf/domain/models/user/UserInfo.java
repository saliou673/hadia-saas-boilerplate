package com.maitrisetcf.domain.models.user;

import com.maitrisetcf.domain.enumerations.UserGender;
import com.maitrisetcf.domain.models.DomainValidation;

import java.time.LocalDate;

public record UserInfo(
        String firstName,
        String lastName,
        String phoneNumber,
        LocalDate birthDate,
        UserGender gender,
        String address,
        String languageKey,
        String imageUrl
) {

    public UserInfo {
        DomainValidation.checkRequiredField(firstName, "firstName");
        DomainValidation.checkRequiredField(lastName, "lastName");
    }

    UserInfo updateInfo(UserInfoUpdate update) {
        return new UserInfo(
                update.firstName(),
                update.lastName(),
                update.phoneNumber(),
                birthDate,
                gender,
                update.address(),
                update.languageKey(),
                update.imageUrl()
        );
    }

}
