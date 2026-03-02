package com.maitrisetcf.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app")
public class ApplicationProperties {
    private Security security;
    private DefaultUser defaultUser;
    private Account account;
    private Mail mail;
    private TwoFactor twoFactor;
    private RateLimit rateLimit;
    private Contact contact;

    public Jwt getJwt() {
        return this.security.authentication().jwt();
    }

    public record Security(Authentication authentication, Cors cors) {}

    public record Authentication(Jwt jwt) {}

    public record Jwt(String base64Secret, Long tokenValidityInSeconds, Long tokenValidityInSecondsForRememberMe) {}

    public record DefaultUser(boolean create, String password) {}

    public record Account(
            String cleanupCron,
            Duration nonActivatedUserRetentionPeriod,
            Duration softDeletedUserRetentionPeriod,
            Duration resetCodeValidityPeriod,
            int managedUserInvitationCodeLength,
            Duration managedUserInvitationCodeValidityPeriod
    ) {}

    public record Mail(String from, MailRoute routes) {}

    public record MailRoute(String accountValidation, String resetPassword, String login,
                            String managedUserInvitation) {}

    public record Cors(List<String> allowedOrigins) {}

    public record TwoFactor(Duration codeValidityPeriod, int codeLength, String totpIssuer) {}

    public record RateLimit(boolean enabled, Auth auth, Api api) {
        public record Auth(int limitForPeriod, Duration limitRefreshPeriod) {}

        public record Api(int limitForPeriod, Duration limitRefreshPeriod) {}
    }

    public record Contact(List<String> recipientEmails) {}
}
