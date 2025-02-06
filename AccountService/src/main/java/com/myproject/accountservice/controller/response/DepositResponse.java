package com.myproject.accountservice.controller.response;

import com.myproject.accountservice.dto.TransactionStatus;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class DepositResponse extends TransactionResponse {
    private BigDecimal moneyInflow;
}
