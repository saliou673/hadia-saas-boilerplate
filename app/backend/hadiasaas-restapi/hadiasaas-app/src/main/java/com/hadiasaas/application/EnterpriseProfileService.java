package com.hadiasaas.application;

import com.hadiasaas.domain.exceptions.EnterpriseProfileNotFoundException;
import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;
import com.hadiasaas.domain.ports.in.EnterpriseProfileUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.EnterpriseProfilePersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link EnterpriseProfileUseCase}: upsert for the singleton enterprise profile. */
public class EnterpriseProfileService implements EnterpriseProfileUseCase {

    private final EnterpriseProfilePersistencePort enterpriseProfilePersistencePort;

    @Override
    public EnterpriseProfile upsert(
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
        log.debug("Upserting enterprise profile");
        EnterpriseProfile profile = enterpriseProfilePersistencePort.find()
                .orElseGet(() -> EnterpriseProfile.create(
                        companyName, legalForm, registrationNumber, vatNumber,
                        addressLine1, addressLine2, city, postalCode, countryCode,
                        phoneNumber, email, website, logoUrl
                ));

        if (profile.getId() != null) {
            profile.update(companyName, legalForm, registrationNumber, vatNumber,
                    addressLine1, addressLine2, city, postalCode, countryCode,
                    phoneNumber, email, website, logoUrl);
        }

        return enterpriseProfilePersistencePort.save(profile);
    }

    @Override
    @Transactional(readOnly = true)
    public EnterpriseProfile get() {
        return enterpriseProfilePersistencePort.find()
                .orElseThrow(() -> new EnterpriseProfileNotFoundException("Enterprise profile has not been configured yet"));
    }
}
