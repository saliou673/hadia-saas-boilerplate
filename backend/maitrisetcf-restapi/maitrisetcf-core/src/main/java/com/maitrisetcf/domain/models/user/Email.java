package com.maitrisetcf.domain.models.user;

import com.maitrisetcf.domain.exceptions.InvalidUserNameException;

import static com.maitrisetcf.domain.constants.DomainConstants.EMAIL_PATTERN;

/**
 * Value object representing a validated, normalized (lowercase) email address.
 *
 * @param value the raw email string; validated and lowercased on construction
 */
public record Email(String value) {

    public Email {
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new InvalidUserNameException("Invalid email format");
        }

        value = value.toLowerCase();
    }
}
