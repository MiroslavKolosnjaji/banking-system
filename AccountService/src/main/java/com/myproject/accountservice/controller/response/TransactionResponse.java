package com.myproject.accountservice.controller.response;

import com.myproject.accountservice.dto.TransactionStatus;
import com.myproject.accountservice.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class TransactionResponse {

    private Long userId;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private TransactionStatus status;
}
