package com.myproject.userservice.service.impl;

import com.myproject.userservice.client.account.AccountClient;
import com.myproject.userservice.client.request.account.CreateAccountRequest;
import com.myproject.userservice.client.response.AccountResponse;
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
import com.myproject.userservice.model.Role;
import com.myproject.userservice.model.User;
import com.myproject.userservice.repository.UserRepository;
import com.myproject.userservice.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * @author Miroslav Kološnjaji
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private RoleService roleService;

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private AccountClient accountClient;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;


    @BeforeEach
    void setUp() {
        userDTO = UserDTO.builder()
                .username("testUser2")
                .password("password")
                .email("test@email.com")
                .role("USER")
                .firstname("John")
                .lastname("Thompson")
                .role("USER")
                .build();


    }

    @DisplayName("Save User")
    @Test
    void testSaveUser_whenValidDetailsProvided_returnsUserDTO() throws UserAlreadyExistsException {

        //given
        String hashPassword = "$2alst2394fas0";

        User user = User.builder()
                .username("testUser2")
                .password("password")
                .email("test@email.com")
                .firstname("John")
                .lastname("Thompson")
                .build();


        user.setPassword(userDTO.getPassword());

        CreateAccountRequest createAccountRequest = mock(CreateAccountRequest.class);
        AccountResponse accountResponse = mock(AccountResponse.class);
        AccountDTO accountDTO = AccountDTO.builder().status("ACTIVE").build();
        userDTO.setAccount(accountDTO);

        Set<RoleDTO> roleDTOSet = Set.of(mock(RoleDTO.class));

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userMapper.userDTOtoUser(userDTO)).thenReturn(user);
        when(roleService.getRolesByRoleNames(userDTO.getRoles())).thenReturn(roleDTOSet);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(hashPassword);
        when(userRepository.save(user)).thenReturn(user);
        when(accountMapper.createAccountDTOToCreateAccountRequest(any(CreateAccountDTO.class))).thenReturn(createAccountRequest);
        when(accountMapper.accountResponseToAccountDTO(any(AccountResponse.class))).thenReturn(accountDTO);
        when(accountClient.createAccount(createAccountRequest)).thenReturn(accountResponse);
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);


        //when
        UserDTO savedDTO = userService.save(userDTO);


        //then
        assertNotNull(savedDTO, "SavedDTO should not be null");
        assertEquals(userDTO, savedDTO, "Saved UserDTO should be equal to userDTO");

        verify(userRepository).findByEmail(anyString());
        verify(roleService).getRolesByRoleNames(userDTO.getRoles());
        verify(userMapper).userDTOtoUser(userDTO);
        verify(userMapper).userToUserDTO(user);
        verify(passwordEncoder).encode(userDTO.getPassword());
        verify(userRepository).save(user);
        verify(accountMapper).createAccountDTOToCreateAccountRequest(any(CreateAccountDTO.class));
        verify(accountMapper).accountResponseToAccountDTO(any(AccountResponse.class));
        verify(accountClient).createAccount(createAccountRequest);
    }

    @DisplayName("Save User Failed - User Already Exists")
    @Test
    void testSaveUser_whenUserAlreadyExists_throwsUserAlreadyExistsException() {

        //given
        User user = getMockUser();
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));

        //when
        Executable executable = () -> userService.save(userDTO);

        //then
        assertThrows(UserAlreadyExistsException.class, executable, "Exception doesn't match. Expected UserAlreadyExistsException");
        verify(userRepository).findByEmail(anyString());
    }

    @DisplayName("Update User")
    @Test
    void testUpdateUser_whenValidDetailsProvided_returnsUserDTO() throws UserNotFoundException {

        //given
        String hashPassword = "$2alst2394fas0";

        User user = User.builder()
                .username("testUser2")
                .password("password")
                .email("test@email.com")
                .firstname("John")
                .lastname("Thompson")
                .build();

        Set<String> roleDTOString = Set.of("ADMIN");
        Set<RoleDTO> roleDTOSet = Set.of(RoleDTO.builder().id(1L).build());
        Set<Role> roleSet = Set.of(Role.builder().id(1L).build());
        userDTO.setRoles(roleDTOString);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(roleService.getRolesByRoleNames(userDTO.getRoles())).thenReturn(roleDTOSet);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn(hashPassword);
        when(roleMapper.setRoleDTOToSetRole(roleDTOSet)).thenReturn(roleSet);
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);

        //when
        UserDTO updatedDTO = userService.update(1L, userDTO);

        //then
        assertNotNull(updatedDTO, "UpdatedDTO should not be null");
        assertEquals(updatedDTO, userDTO, "UpdatedDTO should be equal to userDTO");

        verify(userRepository).findById(anyLong());
        verify(roleService).getRolesByRoleNames(roleDTOString);
        verify(roleMapper).setRoleDTOToSetRole(roleDTOSet);
        verify(passwordEncoder).encode(userDTO.getPassword());
        verify(userMapper).userToUserDTO(user);
    }

    @DisplayName("Update User Failed - User Not Found")
    @Test
    void testUpdateUser_whenUserNotFound_throwsUserNotFoundException() {

        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> userService.update(anyLong(), userDTO);

        //then
        assertThrows(UserNotFoundException.class, executable, "Exception doesn't match. Expected UserNotFoundException");

        verify(userRepository).findById(anyLong());
    }

    @DisplayName("Get All Users")
    @Test
    void testGetAllUsers_whenListIsPopulated_returnsListOfUserDTO() {

        //given
        List<User> users = List.of(getMockUser(), getMockUser());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.userToUserDTO(any(User.class))).thenReturn(userDTO);

        //when
        List<UserDTO> getUsers = userService.getAll();

        //then
        assertNotNull(getUsers, "List should not be null");
        assertEquals(2, getUsers.size(), "List should have 2 elements");

        verify(userRepository).findAll();
        verify(userMapper, times(2)).userToUserDTO(any(User.class));
    }

    @DisplayName("Get User")
    @Test
    void testGetUserById_whenValidIdProvided_returnsUserDTO() throws UserNotFoundException {

        //given
        User user = getMockUser();
        List<AccountResponse> accountResponses = List.of(mock(AccountResponse.class), mock(AccountResponse.class));
        AccountDTO accountDTO = mock(AccountDTO.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(userMapper.userToUserDTO(user)).thenReturn(userDTO);
        when(accountClient.getUserAccounts(anyLong())).thenReturn(accountResponses);
        when(accountMapper.accountResponseToAccountDTO(any(AccountResponse.class))).thenReturn(accountDTO);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        //when
        UserDTO getUser = userService.getById(1L);

        //then
        assertNotNull(getUser, "UserDTO should not be null");
        assertEquals(userDTO, getUser, "GetUser should be equal to userDTO");

        verify(userRepository).findById(anyLong());
        verify(userMapper).userToUserDTO(user);
        verify(accountClient).getUserAccounts(anyLong());
        verify(accountMapper, times(2)).accountResponseToAccountDTO(any(AccountResponse.class));
        verify(userRepository).findById(anyLong());
    }

    @DisplayName("Get User Failed - User Not Found")
    @Test
    void testGetUserById_whenInvalidIdProvided_throwsUserNotFoundException() {

        //given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        //when
        Executable executable = () -> userService.getById(1L);

        //then
        assertThrows(UserNotFoundException.class, executable, "Exception doesn't match. Expected UserNotFoundException");

        verify(userRepository).findById(anyLong());

    }

    @DisplayName("Delete User")
    @Test
    void testDeleteUser_whenValidIdProvided_thenCorrect() throws UserNotFoundException {

        //given
        when(userRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(userRepository).deleteById(anyLong());

        //when
        userService.deleteById(anyLong());

        //then
        verify(userRepository).existsById(anyLong());
        verify(userRepository).deleteById(anyLong());
    }

    @DisplayName("Delete User Failed - User Not Found")
    @Test
    void testDeleteUser_whenUserNotFound_thenThrowsUserNotFoundException() {

        //given
        when(userRepository.existsById(anyLong())).thenReturn(false);

        //given
        Executable executable = () -> userService.deleteById(anyLong());

        //then
        assertThrows(UserNotFoundException.class, executable, "Exception doesn't match. Expected UserNotFoundException");
    }

    @DisplayName("Get User Email Address")
    @Test
    void testGetUserEmail_whenValidIdProvided_returnsTrue() throws UserNotFoundException, EmailNotFoundException {

        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findUserEmailById(anyLong())).thenReturn(Optional.ofNullable(userDTO.getEmail()));

        String email = userService.getUserEmail(anyLong());

        assertEquals(userDTO.getEmail(), email, "Email should be equal.");

        verify(userRepository).existsById(anyLong());

    }

    @DisplayName("Get User Email Address FAILED")
    @Test
    void testGetUserExists_whenInvalidIdProvided_thenThrowUserNotFoundException() throws UserNotFoundException {

        when(userRepository.existsById(anyLong())).thenReturn(false);

        Executable executable = () -> userService.checkIfUserExists(anyLong());

        assertThrows(UserNotFoundException.class, executable, "Exception mismatch. Expected UserNotFoundException.");

        verify(userRepository).existsById(anyLong());
    }

    private User getMockUser() {
        return mock(User.class);
    }

}