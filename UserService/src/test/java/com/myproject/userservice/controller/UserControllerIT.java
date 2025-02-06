package com.myproject.userservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.myproject.userservice.client.account.AccountClient;
import com.myproject.userservice.dto.user.UserDTO;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * @author Miroslav Kološnjaji
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    private static WireMockServer wireMockServer;

    private static final int PORT = 8090;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;

    @BeforeAll
    static void beforeAll() {
        wireMockServer = new WireMockServer(PORT);
        wireMockServer.start();
    }

    @AfterAll
    static void afterAll() {
        if (wireMockServer != null && wireMockServer.isRunning())
            wireMockServer.stop();
    }

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();
        userDTO = UserDTO.builder()
                .username("testUser3")
                .password("password123")
                .email("test@test.com")
                .roles(Set.of())
                .firstname("John")
                .lastname("Smith")
                .build();
    }


    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        String url = "http://localhost:" + wireMockServer.port();

        registry.add("account.service.url", () -> url);
        registry.add("transaction.service.url", () -> url);
    }

    @DisplayName("Wiremock server is running")
    @Test
    void testWireMockServerIsUp() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlPathEqualTo("/external/api"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withBody("{ \"message\": \"success\" }")
                        .withHeader("Content-Type", "application/json")));
    }

    @DisplayName("Create User")
    @Order(1)
    @Test
    void testCreateUser_whenValidDetailsProvided_return201StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.post(WireMock.urlPathEqualTo(AccountClient.BASE_URL))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("user-account.json")));


        mockMvc.perform(post(UserController.USER_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", UserController.USER_URI + "/" + 1))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.firstname").value(userDTO.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(userDTO.getLastname()))
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @DisplayName("Create User Failed - Invalid Details Provided")
    @Test
    void testCreateUser_whenInvalidDetailsProvide_returns400StatusCode() throws Exception {

        userDTO.setPassword(null);

        mockMvc.perform(post(UserController.USER_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Create User Failed - User With Provided Email Already Exist")
    @Order(2)
    @Test
    void testCreateUser_whenUserAlreadyExists_returns409StatusCode() throws Exception {

        mockMvc.perform(post(UserController.USER_URI)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isConflict());

    }

    @DisplayName("Update User")
    @Order(3)
    @Test
    void testUpdateUser_whenValidDetailsProvided_returns204StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching(AccountClient.BASE_URL + "/users/\\d+/accounts"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("get-all-user-accounts.json")));

        userDTO.setPassword("1234567");

        mockMvc.perform(put(UserController.USER_URI_WITH_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Update User Failed - Invalid Details Provided")
    @Order(4)
    @Test
    void testUpdateUser_whenInvalidDetailsProvided_returns400StatusCode() throws Exception {

        userDTO.setPassword(null);

        mockMvc.perform(put(UserController.USER_URI_WITH_ID, 2)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Update User Failed - Invalid Id Provided")
    @Test
    void testUpdateUser_whenInvalidIdProvided_returns400StatusCode() throws Exception {

        mockMvc.perform(put(UserController.USER_URI_WITH_ID, 99)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(objectMapper.writeValueAsString(userDTO)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Get All Users")
    @Order(5)
    @Test
    void testGetAllUsers_whenListIsPopulated_returns200StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching(AccountClient.BASE_URL + "/accounts/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("get-all-user-accounts.json")));

        mockMvc.perform(get(UserController.USER_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(1));
    }

    @DisplayName("Get User By Id")
    @Order(6)
    @Test
    void testGetUserById_whenValidIdProvided_returns200StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching(AccountClient.BASE_URL + "/users/\\d+/accounts"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("get-all-user-accounts.json")));

        mockMvc.perform(get(UserController.USER_URI_WITH_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(userDTO.getUsername()))
                .andExpect(jsonPath("$..email").value(userDTO.getEmail()));
    }

    @DisplayName("Get User By ID Failed - Invalid ID Provided")
    @Test
    void testGetUserById_whenInvalidIdProvided_returns404StatusCode() throws Exception {

        mockMvc.perform(get(UserController.USER_URI_WITH_ID, 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @DisplayName("Delete User By ID")
    @Order(9)
    @Test
    void testDeleteUserById_whenValidIdProvided_returns204StatusCode() throws Exception {

        mockMvc.perform(delete(UserController.USER_URI_WITH_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @DisplayName("Delete User By ID Failed - Invalid ID Provided")
    @Test
    void testDeleteUserById_whenInvalidIdProvided_returns404StatusCode() throws Exception {

        mockMvc.perform(delete(UserController.USER_URI_WITH_ID, 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}