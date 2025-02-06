package com.myproject.transactionservice.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.myproject.transactionservice.controller.request.DepositRequest;
import com.myproject.transactionservice.controller.request.TransactionRequest;
import com.myproject.transactionservice.controller.request.TransferRequest;
import com.myproject.transactionservice.controller.request.WithdrawRequest;
import com.myproject.transactionservice.controller.response.TransactionResponse;
import com.myproject.transactionservice.dto.DepositDTO;
import com.myproject.transactionservice.dto.TransactionDTO;
import com.myproject.transactionservice.dto.TransferDTO;
import com.myproject.transactionservice.dto.WithdrawDTO;
import com.myproject.transactionservice.exception.service.TransactionNotFoundException;
import com.myproject.transactionservice.mapper.TransactionMapper;
import com.myproject.transactionservice.model.Status;
import com.myproject.transactionservice.model.TransactionType;
import com.myproject.transactionservice.service.TransactionService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Miroslav Kološnjaji
 */
@WebMvcTest(controllers = TransactionController.class)
@MockBean(TransactionService.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionMapper transactionMapper;

    @Autowired
    private TransactionService transactionService;

    private ObjectMapper objectMapper;

    private TransactionRequest transactionRequest;
    private TransactionDTO transactionDTO;
    private TransactionResponse transactionResponse;

    @BeforeEach
    void setUp() {

        objectMapper = new ObjectMapper();

        transactionRequest = TransactionRequest.builder()
                .id(1L)
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .status(Status.SUCCESS)
                .amount(new BigDecimal("1234"))
                .currency("RSD")
                .description("Transaction successfully performed.")
                .build();

        transactionDTO = TransactionDTO.builder()
                .id(1L)
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .status(Status.SUCCESS)
                .amount(new BigDecimal("1234"))
                .description("Transaction successfully performed.")
                .build();

        transactionResponse = TransactionResponse.builder()
                .id(1L)
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .status(Status.SUCCESS)
                .amount(new BigDecimal("1234"))
                .description("Transaction successfully performed.")
                .build();
    }

    private String statusMessage(int status) {
        return "Incorrect status code returned, status code " + status + " expected";
    }


    @DisplayName("Get All Transactions")
    @Test
    void testGetAll_whenListIsPopulated_returns200StatusCode() throws Exception {

        List<TransactionDTO> list = List.of(mock(TransactionDTO.class), mock(TransactionDTO.class), mock(TransactionDTO.class));
        List<TransactionResponse> transactionResponses = List.of(mock(TransactionResponse.class), mock(TransactionResponse.class), mock(TransactionResponse.class));

        when(transactionService.getAll()).thenReturn(list);
        when(transactionMapper.transactionDTOListToTransactionResponseList(list)).thenReturn(transactionResponses);


        MvcResult result = mockMvc.perform(get(TransactionController.TRANSACTION_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)).andReturn();

        String response = result.getResponse().getContentAsString();
        List<TransactionResponse> responseList = objectMapper.readValue(response, new TypeReference<>() {});


        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(), statusMessage(200));
        assertNotNull(responseList, "Response list should not be null.");
        assertEquals(list.size(), responseList.size(), "Response List size should be 3.");

        verify(transactionService).getAll();
    }

    @DisplayName("Get Transaction By ID")
    @Test
    void testGetTransactionByID_whenValidIdProvided_returns200StatusCode()throws Exception {

        //given
        when(transactionService.getById(anyLong())).thenReturn(transactionDTO);
        when(transactionMapper.transactionDTOToTransactionResponse(transactionDTO)).thenReturn(transactionResponse);

        //when
        MvcResult result = mockMvc.perform(get(TransactionController.TRANSACTION_URI_WITH_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO))).andReturn();

        String response = result.getResponse().getContentAsString();
        TransactionDTO foundTransaction = objectMapper.readValue(response, TransactionDTO.class);

        //then
        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus(), statusMessage(200));
        assertNotNull(foundTransaction, "Found transaction should not be null.");
        assertEquals(transactionDTO, foundTransaction, "Found transaction doesn't match expected transaction.");

        verify(transactionService).getById(anyLong());
        verify(transactionMapper).transactionDTOToTransactionResponse(transactionDTO);
    }

    @DisplayName("Get Transaction By ID Failed - ID Not Found")
    @Test
    void testGetTransactionByID_whenIdNotFound_returns404StatusCode() throws Exception {

        //given
        when(transactionService.getById(anyLong())).thenThrow(TransactionNotFoundException.class);

        //when
        MvcResult result = mockMvc.perform(get(TransactionController.TRANSACTION_URI_WITH_ID, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(transactionDTO))).andReturn();

        //then
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus(), statusMessage(404));

        verify(transactionService).getById(anyLong());
    }

    @DisplayName("Deposit Transaction")
    @Test
    void testDeposit_whenValidInputProvided_returns201StatusCode() throws Exception {

        //given
        DepositRequest depositRequest = DepositRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(new BigDecimal("100"))
                .build();

        DepositDTO depositDTO = DepositDTO.builder().build();

        TransactionResponse transactionResponse = TransactionResponse.builder().build();

        when(transactionMapper.depositRequestToDepositDTO(depositRequest)).thenReturn(depositDTO);
        when(transactionService.deposit(depositDTO)).thenReturn(transactionDTO);
        when(transactionMapper.transactionDTOToTransactionResponse(transactionDTO)).thenReturn(transactionResponse);

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());

    }

    @DisplayName("Deposit Transaction Failed - Invalid Input")
    @Test
    void testDeposit_whenInvalidInputProvided_returns201StatusCode() throws Exception {

        //given
        DepositRequest depositRequest = DepositRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.DEPOSIT)
                .amount(null)
                .build();

        //when & then
        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isBadRequest());

    }

    @DisplayName("Withdraw Transaction")
    @Test
    void testWithdraw_whenValidInputProvided_returns201StatusCode() throws Exception {

        //given
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(TransactionType.WITHDRAWAL)
                .amount(new BigDecimal("100"))
                .build();

        WithdrawDTO withdrawDTO = WithdrawDTO.builder().build();

        TransactionResponse transactionResponse = TransactionResponse.builder().build();

        when(transactionMapper.withdrawRequestToWithdrawDTO(withdrawRequest)).thenReturn(withdrawDTO);
        when(transactionService.withdraw(withdrawDTO)).thenReturn(transactionDTO);
        when(transactionMapper.transactionDTOToTransactionResponse(transactionDTO)).thenReturn(transactionResponse);

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isCreated());

    }

    @DisplayName("Withdraw Transaction Failed - Invalid Input")
    @Test
    void testWithdraw_whenInvalidInputProvided_returns201StatusCode() throws Exception {

        //given
        WithdrawRequest withdrawRequest = WithdrawRequest.builder()
                .userId(1L)
                .accountId(1L)
                .transactionType(null)
                .amount(new BigDecimal("100"))
                .build();

        //when & then
        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withdrawRequest)))
                .andExpect(status().isBadRequest());

    }

    @DisplayName("Transfer Transaction")
    @Test
    void testTransfer_whenValidInputProvided_returns201StatusCode() throws Exception {

        //given
        TransferRequest transferRequest = TransferRequest.builder()
                .userId(1L)
                .accountId(1L)
                .recipientAccountNumber("418590132943208915408")
                .currency("RSD")
                .transactionType(TransactionType.TRANSFER)
                .amount(new BigDecimal("100"))
                .build();

        TransferDTO transferDTO = TransferDTO.builder().build();

        TransactionResponse transactionResponse = TransactionResponse.builder().build();

        when(transactionMapper.transferRequestToTransferDTO(transferRequest)).thenReturn(transferDTO);
        when(transactionService.transfer(transferDTO)).thenReturn(transactionDTO);
        when(transactionMapper.transactionDTOToTransactionResponse(transactionDTO)).thenReturn(transactionResponse);

        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated());

    }

    @DisplayName("Transfer Transaction Failed - Invalid Input")
    @Test
    void testTransfer_whenInvalidInputProvided_returns201StatusCode() throws Exception {

        //given
        TransferRequest transferRequest = TransferRequest.builder()
                .userId(1L)
                .accountId(1L)
                .recipientAccountNumber("123155132621")
                .currency("RSD")
                .transactionType(null)
                .amount(new BigDecimal("100"))
                .build();


        //when & then
        mockMvc.perform(post(TransactionController.TRANSACTION_URI + "/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest());

    }
}