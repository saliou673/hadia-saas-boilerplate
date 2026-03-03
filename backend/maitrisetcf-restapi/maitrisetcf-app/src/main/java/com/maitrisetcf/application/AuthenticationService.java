package com.maitrisetcf.application;

import com.maitrisetcf.config.ApplicationProperties;
import com.maitrisetcf.domain.exceptions.InvalidRefreshTokenException;
import com.maitrisetcf.domain.exceptions.UserNotFoundException;
import com.maitrisetcf.domain.models.auth.*;
import com.maitrisetcf.domain.models.rbac.Permission;
import com.maitrisetcf.domain.models.user.AuthenticatedUser;
import com.maitrisetcf.domain.models.user.User;
import com.maitrisetcf.domain.ports.in.AuthenticationUseCase;
import com.maitrisetcf.domain.ports.out.JwtTokenPort;
import com.maitrisetcf.domain.ports.out.TwoFactorProviderPort;
import com.maitrisetcf.domain.ports.out.persistenceport.AuthTokenPersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.TwoFactorChallengePersistencePort;
import com.maitrisetcf.domain.ports.out.persistenceport.UserPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
/** Application service implementing {@link AuthenticationUseCase}: login, token refresh, and logout. */
public class AuthenticationService implements AuthenticationUseCase {

    private final JwtTokenPort jwtTokenPort;
    private final AuthTokenPersistencePort authTokenPersistencePort;
    private final UserPersistencePort userPersistencePort;
    private final TwoFactorChallengePersistencePort twoFactorChallengePersistencePort;
    private final List<TwoFactorProviderPort> twoFactorProviders;
    private final ApplicationProperties applicationProperties;

    @Override
    public LoginResult login(String email, String password, boolean rememberMe) {
        AuthenticatedUser authenticatedUser = jwtTokenPort.authenticate(email, password);

        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (user.isTwoFactorEnabled()) {
            return createTwoFactorChallenge(user, rememberMe);
        }

        return completeLogin(authenticatedUser, email, rememberMe);
    }

    @Override
    public JwtToken refreshToken(String refreshTokenValue) {
        AuthToken authToken = authTokenPersistencePort.findByRefreshToken(refreshTokenValue)
                .filter(token -> token.isValid(Instant.now()))
                .orElseThrow(InvalidRefreshTokenException::new);

        User user = authToken.getUser();

        String authorities = user.resolvePermissions()
                .stream()
                .map(Permission::code)
                .collect(Collectors.joining(" "));

        Instant newExpiryDate = jwtTokenPort.calculateTokenValidity(authToken.getRememberMe());

        String accessToken = jwtTokenPort.generateAccessToken(user.getUserCredentials().getEmail(), authorities, newExpiryDate);

        authToken.updateAccessToken(accessToken);
        authToken.updateExpiryDate(newExpiryDate.plus(30, ChronoUnit.DAYS));
        authTokenPersistencePort.save(authToken);

        return new JwtToken(accessToken, refreshTokenValue);
    }

    @Override
    public void logout(String accessToken) {
        authTokenPersistencePort.deleteByAccessToken(accessToken);
    }

    public LoginResult.Complete completeLogin(AuthenticatedUser authenticatedUser, String email, boolean rememberMe) {
        Instant expiryDate = jwtTokenPort.calculateTokenValidity(rememberMe);
        String accessToken = jwtTokenPort.generateAccessToken(
                authenticatedUser.email(),
                authenticatedUser.authorities(),
                expiryDate
        );
        String refreshToken = generateAndSaveRefreshToken(email, accessToken, expiryDate, rememberMe);
        return new LoginResult.Complete(new JwtToken(accessToken, refreshToken));
    }

    private LoginResult.TwoFactorRequired createTwoFactorChallenge(User user, boolean rememberMe) {
        TwoFactorMethodType method = user.getTwoFactorMethod();
        TwoFactorProviderPort provider = getProvider(method);

        String code = provider.generateAndSendCode(user);

        Instant expiryDate = Instant.now().plus(
                applicationProperties.getTwoFactor().codeValidityPeriod()
        );

        twoFactorChallengePersistencePort.deleteByUserId(user.getId());

        TwoFactorChallenge challenge = TwoFactorChallenge.create(
                UUID.randomUUID().toString(),
                user,
                code,
                method,
                TwoFactorChallengePurpose.LOGIN,
                rememberMe,
                expiryDate
        );
        TwoFactorChallenge saved = twoFactorChallengePersistencePort.save(challenge);

        return new LoginResult.TwoFactorRequired(saved.getId(), method);
    }

    private TwoFactorProviderPort getProvider(TwoFactorMethodType type) {
        Map<TwoFactorMethodType, TwoFactorProviderPort> providerMap = twoFactorProviders.stream()
                .collect(Collectors.toMap(TwoFactorProviderPort::getType, Function.identity()));
        TwoFactorProviderPort provider = providerMap.get(type);
        if (provider == null) {
            throw new IllegalStateException("No 2FA provider found for type: " + type);
        }
        return provider;
    }

    private String generateAndSaveRefreshToken(String email, String accessToken, Instant accessTokenExpiryDate, boolean rememberMe) {
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        authTokenPersistencePort.deleteAllForUser(user);

        String tokenValue = UUID.randomUUID().toString();
        Instant expiryDate = accessTokenExpiryDate.plus(30, ChronoUnit.DAYS);

        AuthToken authToken = AuthToken.create(accessToken, tokenValue, rememberMe, expiryDate, user);

        authTokenPersistencePort.save(authToken);
        return tokenValue;
    }
}
