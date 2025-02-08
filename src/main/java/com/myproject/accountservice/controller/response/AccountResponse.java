package com.myproject.accountservice.controller.response;

import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.model.AccountType;
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
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private AccountType accountType;
    private Status status;
}
