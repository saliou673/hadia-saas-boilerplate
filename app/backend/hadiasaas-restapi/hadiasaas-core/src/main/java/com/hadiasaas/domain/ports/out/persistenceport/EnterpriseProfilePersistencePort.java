package com.hadiasaas.domain.ports.out.persistenceport;

import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;

import java.util.Optional;

/**
 * Persistence port for the singleton enterprise profile.
 */
public interface EnterpriseProfilePersistencePort {

    /**
     * Persists or updates the enterprise profile.
     *
     * @param enterpriseProfile the profile to save
     * @return the saved profile
     */
    EnterpriseProfile save(EnterpriseProfile enterpriseProfile);

    /**
     * Returns the enterprise profile if it exists.
     *
     * @return the profile, or empty if not yet created
     */
    Optional<EnterpriseProfile> find();
}
