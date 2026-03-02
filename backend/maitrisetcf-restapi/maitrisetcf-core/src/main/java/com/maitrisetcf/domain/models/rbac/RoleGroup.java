package com.maitrisetcf.domain.models.rbac;

import com.maitrisetcf.domain.models.Auditable;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
public class RoleGroup extends Auditable<Long> {

    private String name;
    private String description;
    private final Set<Permission> permissions;

    private RoleGroup(
            Long id,
            String name,
            String description,
            Set<Permission> permissions,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        super(id, creationDate, lastUpdateDate, lastUpdatedBy);
        this.name = name;
        this.description = description;
        this.permissions = permissions == null ? new HashSet<>() : new HashSet<>(permissions);
    }

    public static RoleGroup create(String name, String description, Set<Permission> permissions) {
        return new RoleGroup(null, name, description, permissions, null, null, null);
    }

    public static RoleGroup rehydrate(
            Long id,
            String name,
            String description,
            Set<Permission> permissions,
            Instant creationDate,
            Instant lastUpdateDate,
            String lastUpdatedBy
    ) {
        return new RoleGroup(id, name, description, permissions, creationDate, lastUpdateDate, lastUpdatedBy);
    }

    public void update(String name, String description, Set<Permission> permissions) {
        this.name = name;
        this.description = description;
        this.permissions.clear();
        if (permissions != null) {
            this.permissions.addAll(permissions);
        }
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(permissions);
    }
}
