package com.myproject.userservice.mapper;

import com.myproject.userservice.dto.user.UserDTO;
import com.myproject.userservice.model.Role;
import com.myproject.userservice.model.RoleName;
import com.myproject.userservice.model.User;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "bankAccounts", ignore = true)
    @Mapping(target = "account", ignore = true)
    UserDTO userToUserDTO(User user);


    @Mapping(target = "roles", source = "roles")
    @Mapping(target = "role", ignore = true)
    User userDTOtoUser(UserDTO userDTO);

    @IterableMapping(elementTargetType = String.class)
    default Set<String> mapRoles(Set<Role> roles){
        return roles.stream().map(role -> role.getName().name()).collect(Collectors.toSet());
    }

    @IterableMapping(elementTargetType = Role.class)
    default Set<Role> mapString(Set<String> roles){
        return roles.stream().map(rolename -> Role.builder().name(RoleName.valueOf(rolename)).build()).collect(Collectors.toSet());
    }
}
