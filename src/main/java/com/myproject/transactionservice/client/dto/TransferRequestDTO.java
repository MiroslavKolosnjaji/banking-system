package com.myproject.transactionservice.client.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;


/**
 * @author Miroslav Kološnjaji
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TransferRequestDTO extends TransactionRequestDTO {

    @NotNull(message = "Account ID required.")
    private Long accountId;

    @NotEmpty(message = "Recipient account number required.")
    private String recipientAccountNumber;

    @NotNull(message = "Currency required.")
    private String currency;
}
