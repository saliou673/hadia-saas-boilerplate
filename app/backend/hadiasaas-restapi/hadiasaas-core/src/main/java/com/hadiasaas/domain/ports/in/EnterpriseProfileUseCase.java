package com.hadiasaas.domain.ports.in;

import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;

/**
 * Use case for managing the singleton enterprise profile.
 */
public interface EnterpriseProfileUseCase {

    /**
     * Creates or updates the enterprise profile (upsert semantics).
     *
     * @param companyName        the company name
     * @param legalForm          optional legal form (SARL, SAS, Ltd…)
     * @param registrationNumber optional registration number (SIRET, etc.)
     * @param vatNumber          optional VAT number
     * @param addressLine1       optional address line 1
     * @param addressLine2       optional address line 2
     * @param city               optional city
     * @param postalCode         optional postal code
     * @param countryCode        optional ISO 3166-1 alpha-2 country code
     * @param phoneNumber        optional phone number
     * @param email              optional email
     * @param website            optional website URL
     * @param logoUrl            optional logo URL
     * @return the saved enterprise profile
     */
    EnterpriseProfile upsert(
            String companyName,
            String legalForm,
            String registrationNumber,
            String vatNumber,
            String addressLine1,
            String addressLine2,
            String city,
            String postalCode,
            String countryCode,
            String phoneNumber,
            String email,
            String website,
            String logoUrl
    );

    /**
     * Returns the current enterprise profile.
     *
     * @return the enterprise profile
     */
    EnterpriseProfile get();
}
