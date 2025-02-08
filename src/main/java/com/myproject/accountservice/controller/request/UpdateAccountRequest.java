package com.myproject.accountservice.controller.request;

import com.myproject.accountservice.model.AccountType;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.sql.Update;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAccountRequest {

    @NotNull(message = "ID is required.")
    private Long id;

    @NotNull(message = "User ID is required.")
    private Long userId;

    @NotBlank(message = "Account number is required.")
    @Size(min = 15, max = 34, message = "Account number size must be between 15 and 34 characters")
    private String accountNumber;

    @NotNull(message = "Account type is required.")
    private AccountType accountType;

    @NotNull(message = "Status must be specified for updating.")
    private Status status;
}
