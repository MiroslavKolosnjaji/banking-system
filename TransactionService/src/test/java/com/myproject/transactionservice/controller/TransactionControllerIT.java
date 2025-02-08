package com.myproject.transactionservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.myproject.transactionservice.client.account.AccountClient;
import com.myproject.transactionservice.client.user.UserClient;
import com.myproject.transactionservice.controller.request.DepositRequest;
import com.myproject.transactionservice.controller.request.TransactionRequest;
import com.myproject.transactionservice.controller.request.TransferRequest;
import com.myproject.transactionservice.controller.request.WithdrawRequest;
import com.myproject.transactionservice.model.Status;
import com.myproject.transactionservice.model.TransactionType;
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
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Miroslav Kološnjaji
 */
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TransactionControllerIT {

    @Container
    @ServiceConnection
    static KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));

    private static WireMockServer wireMockServer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private TransactionRequest transactionRequest;
    private static final int PORT = 8090;

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

    @DynamicPropertySource
    private static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("account.service.url", () -> "http://localhost:" + PORT);
        registry.add("user.service.url", () -> "http://localhost:" + PORT);
        registry.add("spring.kafka.producer.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {


        objectMapper = new ObjectMapper();

        transactionRequest = TransactionRequest.builder()
                .id(1L)
                .userId(1L)
                .accountId(1L)
                .amount(new BigDecimal("50"))
                .currency("RSD")
                .description("TEST")
                .status(Status.SUCCESS)
                .transactionType(TransactionType.DEPOSIT)
                .build();

        wireMockServer.resetAll();
    }

    @Test
    void testWiremockServerIsUp() {
        wireMockServer.stubFor(WireMock.post(WireMock.urlPathEqualTo("/external/api"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withBody("{ \"message\": \"success\" }")
                        .withHeader("Content-Type", "application/json")));
    }

    @DisplayName("Get All Transactions")
    @Order(4)
    @Test
    void testGetAllTransactions_whenListIsPopulated_returns200StatusCode() throws Exception {

        mockMvc.perform(get(TransactionController.TRANSACTION_URI)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(3));
    }

    @DisplayName("Get Transaction By ID")
    @Order(5)
    @Test
    void testGetTransactionById_whenValidIdProvided_returns200StatusCode() throws Exception {

        mockMvc.perform(get(TransactionController.TRANSACTION_URI_WITH_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @DisplayName("Get Transaction By ID Failed - ID Not Found")
    @Test
    void testGetTransactionById_whenInvalidIdProvided_returns404StatusCode() throws Exception {

        mockMvc.perform(get(TransactionController.TRANSACTION_URI_WITH_ID, 99)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }

    @DisplayName("Deposit Transaction")
    @Order(1)
    @Test
    void testDepositTransaction_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching(UserClient.BASE_URL + "/check/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("userExists.json")));

        wireMockServer.stubFor(WireMock.put(WireMock.urlPathMatching(AccountClient.BASE_URL + "/deposit/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("deposit.json")));

        DepositRequest depositRequest = DepositRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("1000"))
                .currency("RSD")
                .build();


        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", TransactionController.TRANSACTION_URI + "/" + 1))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(1000.00))
                .andExpect(jsonPath("$.currency").value("RSD"));

    }

    @DisplayName("Deposit Transaction Failed - Invalid Input")
    @Test
    void testDepositTransaction_whenInvalidDetailsProvided_returns200StatusCode() throws Exception {

        DepositRequest depositRequest = DepositRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(null)
                .build();


        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());
    }

    @DisplayName("Withdraw Transaction")
    @Order(2)
    @Test
    void testWithdrawTransaction_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching(UserClient.BASE_URL + "/check/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("userExists.json")));

        wireMockServer.stubFor(WireMock.put(WireMock.urlPathMatching(AccountClient.BASE_URL + "/withdraw/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("withdraw.json")));

        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("500"))
                .currency("RSD")
                .build();

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", TransactionController.TRANSACTION_URI + "/" + 2))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(500.00))
                .andExpect(jsonPath("$.currency").value("RSD"));

    }

    @DisplayName("Withdraw Transaction Failed - Invalid Input")
    @Test
    void testWithdrawTransaction_whenInvalidDetailsProvided_returns200StatusCode() throws Exception {


        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(null)
                .amount(new BigDecimal("500"))
                .build();

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest());

    }

    @DisplayName("Transfer Transaction")
    @Order(3)
    @Test
    void testTransferTransaction_whenValidDetailsProvided_returns200StatusCode() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathMatching(UserClient.BASE_URL + "/check/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("userExists.json")));

        wireMockServer.stubFor(WireMock.put(WireMock.urlPathMatching(AccountClient.BASE_URL + "/transfer/\\d+"))
                .willReturn(WireMock.aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBodyFile("transfer.json")));

        TransferRequest transferRequest = TransferRequest.builder()
                .userId(1L)
                .accountId(1L)
                .recipientAccountNumber("418590132943208915408")
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("250"))
                .currency("RSD")
                .build();

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", TransactionController.TRANSACTION_URI + "/" + 3))
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(250.00))
                .andExpect(jsonPath("$.currency").value("RSD"));

    }

    @DisplayName("Transfer Transaction Failed - Invalid Input")
    @Test
    void testTransferTransaction_whenInvalidDetailsProvided_returns200StatusCode() throws Exception {

        TransferRequest transferRequest = TransferRequest.builder()
                .userId(1L)
                .accountId(1L)
                .recipientAccountNumber("41859")
                .transactionType(TransactionType.TRANSFER)
                .amount(null)
                .currency("RSD")
                .build();

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());

    }
}