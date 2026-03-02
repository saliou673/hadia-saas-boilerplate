package com.maitrisetcf.domain.models;

import com.maitrisetcf.domain.exceptions.RequiredFieldException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class DomainValidation {

    public static void checkRequiredField(String value, String fieldName) {
        if (StringUtils.isBlank(value)) {
            throw new RequiredFieldException(fieldName + " must not be empty");
        }
    }
}
