package com.hadiasaas.domain.enumerations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Categories used to group application configuration entries.
 */
@Getter
@RequiredArgsConstructor
public enum AppConfigurationCategory {
    /**
     * Supported currency codes (e.g. EUR, USD).
     */
    CURRENCY("Supported currency codes (e.g. EUR, USD)"),
    /**
     * Two-factor authentication provider settings.
     */
    TWO_FACTOR("Two-factor authentication provider settings"),
    /**
     * Supported payment modes (e.g. STRIPE, PAYPAL).
     */
    PAYMENT_MODE("Supported payment modes (e.g. STRIPE, PAYPAL)"),
    /**
     * File storage backend selection (e.g. LOCAL, AWS).
     */
    STORAGE("File storage backend selection (e.g. LOCAL, AWS)"),
    /**
     * Tax configuration values used during billing.
     */
    TAX("Tax configuration values used during billing"),
    /**
     * Enterprise information used in generated business documents.
     */
    ENTERPRISE("Enterprise information used in generated business documents");

    private final String description;
}
