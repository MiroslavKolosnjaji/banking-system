package com.myproject.userservice.service.impl;

import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.exception.controller.EntityNotFoundException;
import com.myproject.userservice.exception.service.RoleNotFoundException;
import com.myproject.userservice.mapper.RoleMapper;
import com.myproject.userservice.model.Role;
import com.myproject.userservice.model.RoleName;
import com.myproject.userservice.repository.RoleRepository;
import com.myproject.userservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author Miroslav Kološnjaji
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleDTO save(RoleDTO roleDTO) {
        throw new UnsupportedOperationException("Save ROLE not supported");
    }

    @Override
    public RoleDTO update(Long id, RoleDTO roleDTO) {
        throw new UnsupportedOperationException("Update ROLE not supported");
    }

    @Override
    public List<RoleDTO> getAll() {
        return roleRepository.findAll().stream().map(roleMapper::roleToRoleDTO).toList();
    }

    @Override
    public RoleDTO getById(Long id) throws EntityNotFoundException {
        Role role = roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException("Role not found!"));
        return roleMapper.roleToRoleDTO(role);
    }

    @Override
    public RoleDTO getByRoleName(RoleName roleName) {

        Role role = roleRepository.getRoleByName(roleName);
        return roleMapper.roleToRoleDTO(role);
    }

    @Override
    public void deleteById(Long id) {
        throw new UnsupportedOperationException("Delete ROLE not supported");
    }

    @Override
    public Set<RoleDTO> findRolesByIds(Set<Long> idSet) {

        Set<Role> roles = roleRepository.findByIdIn(idSet);
        return roleMapper.setRoleToSetRoleDTO(roles);
    }

    @Override
    public Set<RoleDTO> getRolesByRoleNames(Set<String> stringSet) {

        Set<Role> roles = roleRepository.getRolesByRoleNames(stringSet);
        return roleMapper.setRoleToSetRoleDTO(roles);
    }
}
