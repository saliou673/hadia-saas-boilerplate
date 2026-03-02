package com.maitrisetcf.domain.ports.in;

import com.maitrisetcf.domain.models.rbac.Permission;

import java.util.List;

public interface PermissionUseCase {

    List<Permission> findAll();
}
