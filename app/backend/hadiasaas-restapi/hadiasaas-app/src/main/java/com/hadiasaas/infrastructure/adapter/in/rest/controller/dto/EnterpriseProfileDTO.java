package com.hadiasaas.infrastructure.adapter.in.rest.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Schema(name = "EnterpriseProfile")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
/** Response DTO representing the enterprise profile. */
public class EnterpriseProfileDTO extends AuditableDTO {

    private Long id;
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

    public EnterpriseProfileDTO(
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
        super(creationDate, lastUpdateDate, lastUpdatedBy);
        this.id = id;
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
