package com.maitrisetcf.infrastructure.adapter.out.notification;

import static com.maitrisetcf.domain.constants.DomainConstants.DEFAULT_LANGUAGE;

public record NotificationRecipient(
        String firstName,
        String activationCode,
        String resetCode,
        String email,
        String phoneNumber,
        String languageKey
) {

    public NotificationRecipient(String firstName,
                                 String activationCode,
                                 String resetCode,
                                 String email,
                                 String phoneNumber,
                                 String languageKey) {
        this.firstName = firstName;
        this.activationCode = activationCode;
        this.resetCode = resetCode;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.languageKey = languageKey != null ? languageKey : DEFAULT_LANGUAGE;
    }
}

