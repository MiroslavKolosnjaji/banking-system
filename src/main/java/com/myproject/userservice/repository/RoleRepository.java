package com.myproject.userservice.repository;

import com.myproject.userservice.model.Role;
import com.myproject.userservice.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

/**
 * @author Miroslav Kološnjaji
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    Role getRoleByName(RoleName roleName);
    Set<Role> findByIdIn(Set<Long> idSet);

    @Query("SELECT r FROM Role r WHERE r.name IN :nameSet")
    Set<Role> getRolesByRoleNames(@Param("nameSet") Set<String> nameSet);
}
