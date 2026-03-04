package com.maitrisetcf.domain.enumerations;

/**
 * Categories used to group application configuration entries.
 */
public enum AppConfigurationCategory {
    /**
     * Supported currency codes (e.g. EUR, USD).
     */
    CURRENCY,
    /**
     * Two-factor authentication provider settings.
     */
    TWO_FACTOR,
    /**
     * Supported payment modes (e.g. STRIPE, PAYPAL).
     */
    PAYMENT_MODE,
    /**
     * Enterprise information used in generated business documents.
     */
    ENTERPRISE
}
