package com.myproject.userservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.userservice.dto.role.RoleDTO;
import com.myproject.userservice.dto.user.UserDTO;
import com.myproject.userservice.exception.service.UserAlreadyExistsException;
import com.myproject.userservice.exception.service.UserNotFoundException;
import com.myproject.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

/**
 * @author Miroslav Kološnjaji
 */

@WebMvcTest(controllers = UserController.class)
@MockBean(UserService.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    private ObjectMapper objectMapper;
    private UserDTO userDTO;

    private enum RestFulRequest {
        GET,
        POST,
        PUT,
        DELETE
    }

    private RequestBuilder getRequestBuilder(RestFulRequest request) throws Exception {
        return switch (request) {
            case GET ->
                    get(UserController.USER_URI_WITH_ID, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO));
            case PUT ->
                    put(UserController.USER_URI_WITH_ID, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO));
            case POST ->
                    post(UserController.USER_URI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO));
            case DELETE ->
                    delete(UserController.USER_URI_WITH_ID, 1).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(userDTO));
        };
    }

    private RequestBuilder getRequestBuilder(List<UserDTO> list) throws Exception {
        return get(UserController.USER_URI).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(list));

    }

    private String statusMessage(int status) {
        return "Incorrect status code returned, status code " + status + " expected";
    }


    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();

        userDTO = UserDTO.builder()
                .username("testUser2")
                .password("password")
                .email("test@email.com")
                .role("USER")
                .firstname("John")
                .lastname("Thompson")
                .build();
    }

    @DisplayName("Create User")
    @Test
    void testCreateUser_whenValidDetailsProvided_returns201StatusCode() throws Exception {

        //given
        when(userService.save(userDTO)).thenReturn(userDTO);

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.POST)).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        UserDTO savedUser = objectMapper.readValue(response, UserDTO.class);

        //then
        assertEquals(HttpStatus.CREATED.value(), mvcResult.getResponse().getStatus(), statusMessage(201));
        assertNotNull(savedUser, "Saved UserDTO should not be null");
        assertEquals(userDTO, savedUser, "Saved UserDTO is not equal to expected UserDTO");

        verify(userService).save(userDTO);
    }


    @DisplayName("Create User Failed - Invalid input")
    @Test
    void testCreateUser_whenInvalidDetailsProvided_returns400StatusCode() throws Exception {

        //given
        userDTO.setPassword(null);

        //when
        MvcResult result = mockMvc.perform(getRequestBuilder(RestFulRequest.POST)).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus(), statusMessage(400));
    }

    @DisplayName("Create User Failed - User Already Exists")
    @Test
    void testCreateUser_whenUserAlreadyExists_return409StatusCode() throws Exception {

        //given
        when(userService.save(userDTO)).thenThrow(UserAlreadyExistsException.class);

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.POST)).andReturn();

        //then
        assertEquals(HttpStatus.CONFLICT.value(), mvcResult.getResponse().getStatus(), statusMessage(409));

        verify(userService).save(userDTO);
    }

    @DisplayName("Update User")
    @Test
    void testUpdateUser_whenValidDetailsProvided_returns204StatusCode() throws Exception {

        //given
        when(userService.update(anyLong(), any(UserDTO.class))).thenReturn(userDTO);

        //when
        MvcResult result = mockMvc.perform(getRequestBuilder(RestFulRequest.PUT)).andReturn();

        //then
        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus(), statusMessage(204));

        verify(userService).update(anyLong(), any(UserDTO.class));
    }

    @DisplayName("Update User Failed - Invalid Input")
    @Test
    void testUpdateUser_whenInvalidDetailsProvided_thenReturns400StatusCode() throws Exception {

        //given
        userDTO.setPassword(null);

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.PUT)).andReturn();

        //then
        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus(), statusMessage(400));
    }

    @DisplayName("Update User Failed - Invalid ID Provided")
    @Test
    void testUpdateUser_whenInvalidIdProvided_returns404StatusCode() throws Exception {

        //given
        when(userService.update(anyLong(), any(UserDTO.class))).thenThrow(UserNotFoundException.class);

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.PUT)).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), statusMessage(404));

        verify(userService).update(anyLong(), any(UserDTO.class));
    }

    @DisplayName("Get All Users")
    @Test
    void testGetAllUsers_whenListIsPopulated_returns200StatusCode() throws Exception {

        //given
        List<UserDTO> userDTOList = List.of(userDTO, mock(UserDTO.class));
        when(userService.getAll()).thenReturn(userDTOList);

        //when
        MvcResult result = mockMvc.perform(getRequestBuilder(userDTOList)).andReturn();
        String response = result.getResponse().getContentAsString();
        List<UserDTO> responseList = objectMapper.readValue(response, new TypeReference<>() {});

        //then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(), statusMessage(200));
        assertEquals(2, responseList.size(), "List size doesn't match expected userDTOList size");
        assertEquals(userDTO,  responseList.get(0), "First user doesn't match");

        verify(userService).getAll();
    }

    @DisplayName("Get User By ID")
    @Test
    void testGetUserById_whenValidIdProvided_returns200StatusCode() throws Exception {

        //given
        when(userService.getById(anyLong())).thenReturn(userDTO);

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.GET)).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        UserDTO foundDTO = objectMapper.readValue(response, UserDTO.class);

        //then
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), statusMessage(200));
        assertNotNull(foundDTO, "FoundDTO should not be null");
        assertEquals(userDTO, foundDTO, "FoundDTO should be equal to expected userDTO");

        verify(userService).getById(anyLong());
    }

    @DisplayName("Get User by ID Failed - Invalid ID Provided")
    @Test
    void testUserById_whenInvalidIdProvided_returns404StatusCode() throws Exception {

        //given
        when(userService.getById(anyLong())).thenThrow(UserNotFoundException.class);

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.GET)).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), statusMessage(404));

        verify(userService).getById(anyLong());
    }

    @DisplayName("Delete User")
    @Test
    void testDeleteUserById_whenValidIdProvided_thenReturn204StatusCode() throws Exception {

        //given
        doNothing().when(userService).deleteById(anyLong());

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.DELETE)).andReturn();

        //then
        assertEquals(HttpStatus.NO_CONTENT.value(), mvcResult.getResponse().getStatus(), statusMessage(204));

        verify(userService).deleteById(anyLong());
    }

    @DisplayName("Delete User Failed - Invalid ID Provided")
    @Test
    void testDeleteUserById_whenInvalidIdProvided_return404StatusCode() throws Exception {

        //given
        doThrow(UserNotFoundException.class).when(userService).deleteById(anyLong());

        //when
        MvcResult mvcResult = mockMvc.perform(getRequestBuilder(RestFulRequest.DELETE)).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), mvcResult.getResponse().getStatus(), statusMessage(404));

        verify(userService).deleteById(anyLong());
    }

    @DisplayName("Check If User Exists")
    @Test
    void testIsUserExists_whenValidIdProvided_returns200StatusCode() throws Exception {

        when(userService.checkIfUserExists(anyLong())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(get(UserController.USER_URI_CHECK_USER, 1)
                .contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        boolean result = Boolean.parseBoolean(response);

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), statusMessage(200));
        assertTrue(result, "Result should return true.");

    }

    @DisplayName("Check If User Exists FAILED")
    @Test
    void testaIsUserExists_whenValidIdProvided_returns200StatusCode() throws Exception {

        when(userService.checkIfUserExists(anyLong())).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(get(UserController.USER_URI_CHECK_USER, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(true))).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        boolean result = objectMapper.readValue(response, Boolean.class);

        assertTrue(result, "Result should return true.");
    }
}

