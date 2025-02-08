package com.myproject.accountservice.dto;

import com.myproject.accountservice.model.AccountType;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {

    private Long id;
    private String accountNumber;
    private Long userId;
    private BigDecimal balance;
    private Currency currency;
    private AccountType accountType;
    private Status status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
