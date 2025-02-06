package com.myproject.accountservice.controller.request;

import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.model.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountRequest {

    @NotNull(message = "User ID must be provided.")
    private Long userId;

    @NotNull(message = "Account type required.")
    private AccountType accountType;

    @NotNull(message = "Currency type required.")
    private Currency currency;
}
