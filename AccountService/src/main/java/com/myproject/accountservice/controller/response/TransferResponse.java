package com.myproject.accountservice.controller.response;

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
public class TransferResponse extends TransactionResponse {
    private BigDecimal moneyOutflow;
}
