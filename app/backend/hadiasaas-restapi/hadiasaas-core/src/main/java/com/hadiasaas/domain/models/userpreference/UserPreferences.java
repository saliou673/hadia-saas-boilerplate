package com.hadiasaas.domain.models.userpreference;

import java.util.Objects;

/**
 * Structured user preferences persisted as a JSON document.
 */
public record UserPreferences(AppearancePreferences appearance) {

    public UserPreferences {
        Objects.requireNonNull(appearance, "appearance must not be null");
    }

    public static UserPreferences of(AppearancePreferences appearance) {
        return new UserPreferences(appearance);
    }

    public static UserPreferences defaults() {
        return new UserPreferences(AppearancePreferences.defaultPreferences());
    }
}
