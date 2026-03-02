package com.maitrisetcf.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Application constants.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Constants {

    public static final String PASSWORD_REGEX_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$ %^&*-]).{8,}$";

    public static final String DEFAULT_REST_PAGE_SIZE = "20";
    public static final String DEFAULT_REST_PAGE_NUMBER = "0";
}
