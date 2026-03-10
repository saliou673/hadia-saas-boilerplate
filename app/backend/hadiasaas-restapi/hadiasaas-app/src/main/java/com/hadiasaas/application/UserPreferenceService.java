package com.hadiasaas.application;

import com.hadiasaas.domain.exceptions.UserNotFoundException;
import com.hadiasaas.domain.models.user.User;
import com.hadiasaas.domain.models.userpreference.UserPreferences;
import com.hadiasaas.domain.ports.in.UserPreferenceUseCase;
import com.hadiasaas.domain.ports.out.CurrentUserEmailPort;
import com.hadiasaas.domain.ports.out.persistenceport.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for the authenticated user's preferences.
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserPreferenceService implements UserPreferenceUseCase {

    private final UserPersistencePort userPersistencePort;
    private final CurrentUserEmailPort currentUserEmailPort;

    @Transactional(readOnly = true)
    @Override
    public UserPreferences getCurrentUserPreferences() {
        return getCurrentUser().getPreferences();
    }

    @Override
    public UserPreferences updateCurrentUserPreferences(UserPreferences preferences) {
        User user = getCurrentUser();
        user.updatePreferences(preferences);
        log.debug("Updating preferences for user id={}", user.getId());
        return userPersistencePort.save(user).getPreferences();
    }

    private User getCurrentUser() {
        String email = currentUserEmailPort.getCurrentUserEmail();
        return userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
    }
}
