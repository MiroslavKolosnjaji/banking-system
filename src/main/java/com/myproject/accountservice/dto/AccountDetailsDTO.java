package com.myproject.accountservice.dto;

import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
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
public class AccountDetailsDTO {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Currency currency;
    private Status status;
}
