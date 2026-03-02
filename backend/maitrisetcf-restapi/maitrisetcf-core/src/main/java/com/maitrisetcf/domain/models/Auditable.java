package com.maitrisetcf.domain.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
public class Auditable<T> {
    private T id;
    private Instant creationDate;
    private Instant lastUpdateDate;
    private String lastUpdatedBy;
}
