package com.myproject.userservice.bootstrap;

import com.myproject.userservice.dto.user.UserDTO;
import com.myproject.userservice.exception.controller.EntityAlreadyExistsException;
import com.myproject.userservice.model.Role;
import com.myproject.userservice.model.RoleName;
import com.myproject.userservice.repository.RoleRepository;
import com.myproject.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Component
@RequiredArgsConstructor
//@Profile("dev")
public class BootstrapData implements CommandLineRunner {

    private final UserService userService;

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        loadRoleData();

//      loadUserData is disabled because it interferes with integration tests.
//      Enable it only for development purposes, otherwise keep it disabled.
//        loadUserData();
    }

    private void loadUserData() throws EntityAlreadyExistsException {
        UserDTO userDTO = UserDTO.builder()
                .username("testUser")
                .password("testPassword")
                .email("test@example.com")
                .firstname("John")
                .lastname("Smith")
                .role(null)
                .build();

        userService.save(userDTO);
    }

    private void loadRoleData() {

        Role admin = Role.builder().name(RoleName.ADMIN).build();
        Role user = Role.builder().name(RoleName.USER).build();

        if (roleRepository.count() == 0) {
            roleRepository.saveAll(List.of(admin, user));
            roleRepository.flush();
        }


        long count = roleRepository.count();
        log.info("Repo count is: {}", count);
    }
}
