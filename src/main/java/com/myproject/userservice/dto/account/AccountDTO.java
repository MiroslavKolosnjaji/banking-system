package com.myproject.userservice.dto.account;

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
public class AccountDTO {
    private String accountNumber;
    private Long userId;
    private BigDecimal balance;
    private String currency;
    private String accountType;
    private String status;
    private String createdAt;
    private String updatedAt;
}
