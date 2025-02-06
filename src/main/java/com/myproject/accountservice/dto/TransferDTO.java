package com.myproject.accountservice.dto;

import com.myproject.accountservice.model.Currency;
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
public class TransferDTO {

    private Long userId;
    private String recipientAccountNumber;
    private BigDecimal amount;
    private Currency currency;
}
