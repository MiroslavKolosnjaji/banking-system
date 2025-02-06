package com.myproject.userservice.service.impl;

import com.myproject.userservice.client.account.AccountClient;
import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.exception.controller.EntityNotFoundException;
import com.myproject.userservice.exception.service.RoleNotFoundException;
import com.myproject.userservice.model.RoleName;
import com.myproject.userservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
class RoleServiceImplIT {

    @Autowired
    private RoleService roleService;

    //TODO: Temporary solution, proper test isolation or redesign needed.
    @MockBean
    private AccountClient accountClient;

    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        roleDTO = RoleDTO.builder().name(RoleName.ADMIN).build();
    }

    @DisplayName("Get All Roles")
    @Test
    void testGetAllRoles_whenListIsPopulated_returnRoleDTOList() {

        //given & when
        List<RoleDTO> roleDTOList = roleService.getAll();

        //then
        assertNotNull(roleDTOList, "RoleDTOList should not be null");
        assertEquals(2, roleDTOList.size(), "RoleDTOList size not match, expected 2");
    }

    @DisplayName("Get Role By ID")
    @Test
    void testGetRoleById_whenValidIdProvided_returnRoleDTO() throws EntityNotFoundException {

        //given & when
        RoleDTO foundDTO = roleService.getById(1L);

        //then
        assertNotNull(foundDTO, "FoundDTO should not be null");
        assertEquals(RoleName.ADMIN, foundDTO.getName(), "Expected Role name to be ADMIN, but foundDTO.getName() returned a different value.");
    }

    @DisplayName("Get Role By ID Failed - Invalid ID Provided")
    @Test
    void testGetRoleById_whenInvalidIDProvided_throwsRoleNotFoundException() {

        //given & when
        Executable executable = () -> roleService.getById(99L);

        //then
        assertThrows(RoleNotFoundException.class, executable, "Expected RoleNotFoundException to be thrown, but a different exception was caught or none was thrown.");
    }

    @DisplayName("Get Role By Name")
    @Test
    void testGetRoleByName_whenValidRoleNameProvided_returnRoleDTO() {

        //given && when
        RoleDTO foundDTO = roleService.getByRoleName(RoleName.USER);

        //then
        assertNotNull(foundDTO, "FoundDTO should not be null");
        assertEquals(RoleName.USER, foundDTO.getName(),"Expected Role name to be USER, but foundDTO.getName() returned a different value." );
    }

    @DisplayName("Get Roles By IDS")
    @Test
    void testGetRolesByIDS_whenValidSetProvided_returnRoleDTOSet() {

        //given & when
        Set<RoleDTO> roleDTOSet = roleService.findRolesByIds(Set.of(1L, 2L));

        //then
        assertNotNull(roleDTOSet, "RoleDTOSet should not be null");
        assertEquals(2, roleDTOSet.size(), "Expected size to be 2, but found a different value");
    }
}