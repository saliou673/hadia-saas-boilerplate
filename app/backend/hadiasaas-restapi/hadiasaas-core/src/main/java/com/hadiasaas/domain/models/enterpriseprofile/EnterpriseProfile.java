package com.hadiasaas.domain.models.enterpriseprofile;

import com.hadiasaas.domain.models.Auditable;
import lombok.Getter;

import java.time.Instant;

/**
 * Domain entity representing the enterprise (company) profile — singleton row.
 */
@Getter
public class EnterpriseProfile extends Auditable<Long> {

    private String companyName;
    private String legalForm;
    private String registrationNumber;
    private String vatNumber;
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String postalCode;
    private String countryCode;
    private String phoneNumber;
    private String email;
    private String website;
    private String logoUrl;

    private EnterpriseProfile(
            Long id,
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
            String logoUrl,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.companyName = companyName;
        this.legalForm = legalForm;
        this.registrationNumber = registrationNumber;
        this.vatNumber = vatNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.logoUrl = logoUrl;
    }

    public static EnterpriseProfile create(
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
    ) {
        return new EnterpriseProfile(null, companyName, legalForm, registrationNumber, vatNumber,
                addressLine1, addressLine2, city, postalCode, countryCode, phoneNumber, email, website, logoUrl,
                null, null, null);
    }

    public static EnterpriseProfile rehydrate(
            Long id,
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
            String logoUrl,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new EnterpriseProfile(id, companyName, legalForm, registrationNumber, vatNumber,
                addressLine1, addressLine2, city, postalCode, countryCode, phoneNumber, email, website, logoUrl,
                creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(
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
    ) {
        this.companyName = companyName;
        this.legalForm = legalForm;
        this.registrationNumber = registrationNumber;
        this.vatNumber = vatNumber;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.postalCode = postalCode;
        this.countryCode = countryCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.logoUrl = logoUrl;
    }
}
