package com.maitrisetcf.domain.enumerations;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public final class UserGroupConstants {

    public static final String SYS_ADMIN = "Sysadmin";
    public static final String ADMIN = "Admin";
    public static final String USER = "User";
    public static final String INTERNAL = "Internal";
    public static final String EXTERNAL_INSTITUTION = "External";
    public static final String ANONYMOUS = "Anonymous";
}
