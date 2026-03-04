package com.hadiasaas.config;


import com.hadiasaas.domain.constants.DomainConstants;
import com.hadiasaas.domain.enumerations.UserGender;
import com.hadiasaas.domain.enumerations.UserGroupConstants;
import com.hadiasaas.domain.models.rbac.RoleGroup;
import com.hadiasaas.domain.models.user.User;
import com.hadiasaas.domain.models.user.UserCredentials;
import com.hadiasaas.domain.models.user.UserInfo;
import com.hadiasaas.domain.ports.out.PasswordHasherPort;
import com.hadiasaas.domain.ports.out.persistenceport.RoleGroupPersistencePort;
import com.hadiasaas.domain.ports.out.persistenceport.UserPersistencePort;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;


/**
 * Creates the default sysadmin user on startup when running outside the test profile.
 */
@Profile("!test")
@Component
@RequiredArgsConstructor
public class DefaultUserInitializer {

    private final ApplicationProperties applicationProperties;
    private final UserPersistencePort userPersistencePort;
    private final RoleGroupPersistencePort roleGroupPersistencePort;
    private final PasswordHasherPort passwordHasherPort;

    @PostConstruct
    private void init() {
        if (applicationProperties.getDefaultUser().create()) {
            createUser("sysadmin@dev.com", "SysAdmin", UserGroupConstants.SYS_ADMIN);
            createUser("admin@dev.com", "Admin", UserGroupConstants.ADMIN);
            createUser("user@dev.com", "User", UserGroupConstants.USER);
        }
    }

    private void createUser(String email, String firstName, String userGroup) {

        // Dot doesn't create the user if it already exists.
        if (userPersistencePort.findByEmail(email).isPresent()) {
            return;
        }

        userPersistencePort.save(createUserObject(email, firstName, userGroup));
    }

    private User createUserObject(String email, String firstName, String roleGroupName) {
        String passwordHash = passwordHasherPort.hash(applicationProperties.getDefaultUser().password());
        UserInfo userInfo = new UserInfo(
                firstName,
                "Dev",
                null,
                LocalDate.of(1990, 1, 1),
                UserGender.MALE,
                "Guinée",
                DomainConstants.DEFAULT_LANGUAGE,
                null
        );
        UserCredentials credentials = new UserCredentials(
                email,
                passwordHash,
                null,
                null,
                null,
                null
        );
        User user = User.create(userInfo, credentials);
        user.assignRoleGroups(getRoleGroups(roleGroupName));
        user.activate(Instant.now());
        return user;
    }

    private Set<RoleGroup> getRoleGroups(String roleGroupName) {
        Set<RoleGroup> roleGroups = roleGroupPersistencePort.findByNames(Set.of(roleGroupName));
        if (roleGroups.isEmpty()) {
            throw new IllegalStateException("Role group not found: " + roleGroupName);
        }
        return roleGroups;
    }
}
