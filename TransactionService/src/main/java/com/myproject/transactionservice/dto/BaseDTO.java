package com.myproject.transactionservice.dto;

import com.myproject.transactionservice.model.TransactionType;
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
public abstract class BaseDTO {

    private Long userId;
    private Long accountId;
    private BigDecimal amount;
    private String currency;
    private TransactionType transactionType;
}
