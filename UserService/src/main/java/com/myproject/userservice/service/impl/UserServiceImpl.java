package com.myproject.userservice.service.impl;

import com.myproject.userservice.client.account.AccountClient;
import com.myproject.userservice.client.request.account.CreateAccountRequest;
import com.myproject.userservice.dto.account.AccountDTO;
import com.myproject.userservice.dto.account.CreateAccountDTO;
import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.dto.user.UserDTO;
import com.myproject.userservice.exception.service.EmailNotFoundException;
import com.myproject.userservice.exception.service.UserAlreadyExistsException;
import com.myproject.userservice.exception.service.UserNotFoundException;
import com.myproject.userservice.mapper.AccountMapper;
import com.myproject.userservice.mapper.RoleMapper;
import com.myproject.userservice.mapper.UserMapper;
import com.myproject.userservice.model.User;
import com.myproject.userservice.repository.UserRepository;
import com.myproject.userservice.service.RoleService;
import com.myproject.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author Miroslav Kološnjaji
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleService roleService;
    private final AccountClient accountClient;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public UserDTO save(UserDTO userDTO) throws UserAlreadyExistsException {

        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());

        if (existingUser.isPresent())
            throw new UserAlreadyExistsException("User with this email address already exists!");

        User user = userMapper.userDTOtoUser(userDTO);

        Set<RoleDTO> roleDTOSet = roleService.getRolesByRoleNames(userDTO.getRoles());

        user.setRoles(roleMapper.setRoleDTOToSetRole(roleDTOSet));
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);

        CreateAccountDTO createAccountDTO = getCreateAccountDTO(savedUser);

        CreateAccountRequest accountRequest = accountMapper.createAccountDTOToCreateAccountRequest(createAccountDTO);
        AccountDTO accountDTO = accountMapper.accountResponseToAccountDTO(accountClient.createAccount(accountRequest));

        UserDTO savedUserDTO = userMapper.userToUserDTO(savedUser);
        savedUserDTO.setAccount(accountDTO);
        return savedUserDTO;
    }

    @Override
    @Transactional
    public UserDTO update(Long id, UserDTO userDTO) throws UserNotFoundException {

        User existingUser = getUserById(id);

        Set<RoleDTO> roles = roleService.getRolesByRoleNames(userDTO.getRoles());

        existingUser.setUsername(userDTO.getUsername());
        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setFirstname(userDTO.getFirstname());
        existingUser.setLastname(userDTO.getLastname());
        existingUser.setRoles(roleMapper.setRoleDTOToSetRole(roles));

        return userMapper.userToUserDTO(existingUser);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream().map(userMapper::userToUserDTO).toList();
    }

    @Transactional
    @Override
    public UserDTO getById(Long id) throws UserNotFoundException {

        User user = getUserById(id);

        List<AccountDTO> accounts = accountClient.getUserAccounts(id).stream()
                .map(accountMapper::accountResponseToAccountDTO).toList();

        UserDTO userDTO = userMapper.userToUserDTO(user);
        userDTO.setBankAccounts(accounts);

        return userDTO;
    }

    @Transactional
    @Override
    public void deleteById(Long id) throws UserNotFoundException {

        checkIfUserExists(id);

        userRepository.deleteById(id);
    }

    public String getUserEmail (Long id) throws UserNotFoundException, EmailNotFoundException {

        checkIfUserExists(id);

        return userRepository.findUserEmailById(id)
                .orElseThrow(() -> new EmailNotFoundException("Email not found for user with ID " + id));
    }

    void checkIfUserExists(Long id) throws UserNotFoundException{
        if (!userRepository.existsById(id))
            throw new UserNotFoundException("User not found");
    }

    private User getUserById(Long id) throws UserNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found!"));
    }

    private static CreateAccountDTO getCreateAccountDTO(User savedUser) {
        return CreateAccountDTO.builder()
                .userId(savedUser.getId())
                .accountType("CHECKING")
                .status("ACTIVE")
                .currency("RSD")
                .build();
    }

}
