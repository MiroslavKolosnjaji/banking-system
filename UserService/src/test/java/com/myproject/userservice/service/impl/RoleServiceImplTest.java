package com.myproject.userservice.service.impl;

import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.exception.service.RoleNotFoundException;
import com.myproject.userservice.mapper.RoleMapper;
import com.myproject.userservice.model.Role;
import com.myproject.userservice.model.RoleName;
import com.myproject.userservice.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl roleService;

    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        roleDTO = RoleDTO.builder().name(RoleName.USER).build();
    }

    @DisplayName("Get All Roles")
    @Test
    void testGetAllRoles_whenListIsPopulated_returnsRoleDTOList() {

        //given
        List<Role> roles = List.of(mock(Role.class), mock(Role.class));
        when(roleRepository.findAll()).thenReturn(roles);

        //when
        List<RoleDTO> roleDTOS = roleService.getAll();

        //then
        assertNotNull(roleDTOS, "RoleDTO list should not be null");
        assertEquals(2, roleDTOS.size(), "Size of roleDTOS doesn't match expected size");

        verify(roleRepository).findAll();
    }

    @DisplayName("Get Role By ID")
    @Test
    void testGetRoleById_whenValidIdProvided_returnsRoleDTO() throws Exception {

        //given
        Role role = Role.builder().name(RoleName.USER).build();
        when(roleRepository.findById(anyLong())).thenReturn(Optional.of(role));
        when(roleMapper.roleToRoleDTO(role)).thenReturn(roleDTO);

        //when
        RoleDTO foundRoleDTO = roleService.getById(anyLong());

        //then
        assertNotNull(foundRoleDTO, "FoundRoleDTO should not be null");
        assertEquals(role.getName(), foundRoleDTO.getName(), "FoundRoleDTO name is not same as expected.");

        verify(roleRepository).findById(anyLong());
        verify(roleMapper).roleToRoleDTO(role);
    }

    @DisplayName("Get Role By ID Failed - Invalid ID Provided")
    @Test
    void testGetRoleById_whenInvalidIdProvided_throwsRoleNotFoundException() {

        //given
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> roleService.getById(anyLong());

        assertThrows(RoleNotFoundException.class, executable, "Exception mismatch. RoleNotFoundException expected");

        verify(roleRepository).findById(anyLong());
    }

    @DisplayName("Get Role By Name")
    @Test
    void testGetRoleByName_whenValidRoleNameProvided_returnsRoleDTO() {

        //given
        Role role = Role.builder().name(RoleName.USER).build();
        when(roleRepository.getRoleByName(RoleName.USER)).thenReturn(role);
        when(roleMapper.roleToRoleDTO(role)).thenReturn(roleDTO);

        //when
        RoleDTO foundDTO = roleService.getByRoleName(RoleName.USER);

        assertNotNull(foundDTO, "FoundDTO should not be null");
        assertEquals(RoleName.USER, foundDTO.getName(), "Expected RoleName.User but found a different value");

        verify(roleRepository).getRoleByName(RoleName.USER);
        verify(roleMapper).roleToRoleDTO(role);
    }

    @DisplayName("Get Roles By ID- IN")
    @Test
    void testFindRolesByIDS_whenValidSetProvided_returnsRoleDTOSet() {

        //given
        Set<Role> roleSet = Set.of(Role.builder().name(RoleName.USER).build());
        Set<RoleDTO> roleDTOSet  = Set.of(roleDTO);

        when(roleRepository.findByIdIn(Set.of(1L))).thenReturn(roleSet);
        when(roleMapper.setRoleToSetRoleDTO(roleSet)).thenReturn(roleDTOSet);

        //when
        Set<RoleDTO> foundSet = roleService.findRolesByIds(Set.of(1L));

        //then
        assertNotNull(foundSet, "FoundSet should not be null");
        assertEquals(1, foundSet.size(), "Expected size of 1 but found a different value");
    }
}