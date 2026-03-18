package com.hadiasaas.application;

import com.hadiasaas.domain.models.securitysettings.SecuritySettings;
import com.hadiasaas.domain.ports.in.SecuritySettingsUseCase;
import com.hadiasaas.domain.ports.out.persistenceport.SecuritySettingsPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link SecuritySettingsUseCase}: upsert for the singleton security settings. */
public class SecuritySettingsService implements SecuritySettingsUseCase {

    private final SecuritySettingsPersistencePort securitySettingsPersistencePort;

    @Override
    public SecuritySettings upsert(boolean twoFactorRequired) {
        log.debug("Upserting security settings");
        SecuritySettings settings = securitySettingsPersistencePort.find()
                .orElseGet(() -> SecuritySettings.create(twoFactorRequired));

        if (settings.getId() != null) {
            settings.update(twoFactorRequired);
        }

        return securitySettingsPersistencePort.save(settings);
    }

    @Override
    @Transactional(readOnly = true)
    public SecuritySettings get() {
        return securitySettingsPersistencePort.find()
                .orElseGet(() -> SecuritySettings.create(false));
    }
}
