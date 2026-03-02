package com.maitrisetcf.config;

import com.maitrisetcf.infrastructure.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

/**
 * By default, all users will speak french. The i18n will come in next versions.
 * It ensures that the right locale is used for the user.
 */
@Configuration
public class LocaleConfiguration {
    @Bean
    public LocaleResolver localeResolver() {
        return new AcceptHeaderLocaleResolver() {
            @Override
            public Locale resolveLocale(HttpServletRequest request) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // Check if the user is authenticated and has a language key
                if (authentication != null && authentication.getPrincipal() instanceof AuthenticatedUser user) {
                    String languageKey = user.getLanguageKey();
                    if (StringUtils.isNotBlank(languageKey)) {
                        return Locale.forLanguageTag(languageKey);
                    }
                }

                // Fallback to standard header-based resolution
                return super.resolveLocale(request);
            }
        };
    }
}
