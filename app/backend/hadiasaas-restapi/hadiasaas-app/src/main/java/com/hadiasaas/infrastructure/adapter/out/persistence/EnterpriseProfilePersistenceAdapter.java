package com.hadiasaas.infrastructure.adapter.out.persistence;

import com.hadiasaas.domain.models.enterpriseprofile.EnterpriseProfile;
import com.hadiasaas.domain.ports.out.persistenceport.EnterpriseProfilePersistencePort;
import com.hadiasaas.infrastructure.adapter.out.persistence.mapper.EnterpriseProfileMapper;
import com.hadiasaas.infrastructure.adapter.out.persistence.repository.EnterpriseProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * JPA adapter implementing {@link EnterpriseProfilePersistencePort}.
 * Uses findFirst to retrieve the singleton row.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class EnterpriseProfilePersistenceAdapter implements EnterpriseProfilePersistencePort {

    private final EnterpriseProfileRepository enterpriseProfileRepository;
    private final EnterpriseProfileMapper enterpriseProfileMapper;

    @Override
    public EnterpriseProfile save(EnterpriseProfile enterpriseProfile) {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> enterpriseProfileMapper.toDomain(enterpriseProfileRepository.save(enterpriseProfileMapper.toEntity(enterpriseProfile))),
                "Error saving enterprise profile"
        );
    }

    @Override
    public Optional<EnterpriseProfile> find() {
        return AdapterPersistenceUtils.executeDbOperation(
                () -> enterpriseProfileRepository.findAll().stream()
                        .findFirst()
                        .map(enterpriseProfileMapper::toDomain),
                "Error fetching enterprise profile"
        );
    }
}
