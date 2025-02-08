package com.myproject.accountservice.dto;

import com.myproject.accountservice.model.Currency;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawDTO {

    private Long userId;
    private BigDecimal amount;
    private Currency currency;
}
