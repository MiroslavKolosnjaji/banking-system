package com.myproject.transactionservice.client.user;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
class UserClientTest {

    private MockWebServer mockWebServer;
    private UserClient userClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        objectMapper = new ObjectMapper();

        WebClient.Builder webclientBuilder = WebClient.builder();
        String baseUrl = mockWebServer.url("/").toString();
        userClient = new UserClient(webclientBuilder, new UserClientProperties(baseUrl));
    }

    @Test
    void testIsUserExists_whenIdIsProvided_returns200StatusCode() throws Exception {

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.OK.value())
                .setBody("test@example.com")
                .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        String result = userClient.isUserExists(1L);

        assertEquals("test@example.com", result, "Email address doesn't match.");

        mockWebServer.takeRequest();
    }
}