package com.myproject.userservice.service;

import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.model.RoleName;

import java.util.Set;

/**
 * @author Miroslav Kološnjaji
 */
public interface RoleService extends BaseCRUD<RoleDTO, Long> {

    RoleDTO getByRoleName(RoleName roleName);
    Set<RoleDTO> findRolesByIds(Set<Long> collect);
    Set<RoleDTO> getRolesByRoleNames(Set<String> stringSet);
}
