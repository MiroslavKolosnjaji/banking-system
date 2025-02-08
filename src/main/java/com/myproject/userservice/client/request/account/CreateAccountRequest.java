package com.myproject.userservice.client.request.account;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAccountRequest {

    @NotNull(message = "User ID required.")
    private Long userId;

    @NotEmpty(message = "Account number required.")
    @Pattern(regexp = "^(SAVINGS|CHECKING)$", message = "Invalid account type. Only SAVINGS or CHECKING are allowed.")
    private String accountType;

    @NotEmpty(message = "Currency type required.")
    @Pattern(regexp = "^(EUR|GBP|CHF|USD|RSD)$", message = "Invalid currency type. Only EUR, GBP, CHF, USD, or RSD are allowed.")
    private String currency;
}
