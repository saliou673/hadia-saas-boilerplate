package com.hadiasaas.domain.models.user;

/**
 * Command carrying the fields a user may update on their own profile.
 *
 * @param firstName   new given name
 * @param lastName    new family name
 * @param phoneNumber new phone number (optional)
 * @param address     new postal address (optional)
 * @param languageKey new preferred locale key (optional)
 * @param imageUrl    new profile picture URL (optional)
 */
public record UserInfoUpdate(
        String firstName,
        String lastName,
        String phoneNumber,
        String address,
        String languageKey,
        String imageUrl
) {

}

