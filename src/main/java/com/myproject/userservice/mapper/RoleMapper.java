package com.myproject.userservice.mapper;

import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.model.Role;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper
public interface RoleMapper {

    RoleDTO roleToRoleDTO(Role role);


    @Mapping(target = "users", ignore = true)
    Role roleDTOtoRole(RoleDTO roleDTO);

    @IterableMapping(elementTargetType = RoleDTO.class)
    Set<RoleDTO> setRoleToSetRoleDTO(Set<Role> roleSet);

    @IterableMapping(elementTargetType = Role.class)
    Set<Role> setRoleDTOToSetRole(Set<RoleDTO> roleDTOSet);

}
