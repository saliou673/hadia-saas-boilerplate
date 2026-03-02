package com.maitrisetcf.domain.models.user;

public record UserInfoUpdate(
        String firstName,
        String lastName,
        String phoneNumber,
        String address,
        String languageKey,
        String imageUrl
) {

}

