package com.myproject.transactionservice.dto;

import com.myproject.transactionservice.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDetailsDTO {


    private String accountNumber;
    private BigDecimal amount;
    private BigDecimal balance;
    private String currency;
    private Status status;
    private String description;
}
