package com.myproject.accountservice.controller.request;

import com.myproject.accountservice.model.Currency;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * @author Miroslav Kološnjaji
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class TransferRequest extends BaseRequest{

    @NotEmpty(message = "Recipient account number required.")
    @Size(min = 15, max = 34, message = "Account number size must be between 15 and 34 characters")
    private String recipientAccountNumber;

    @NotNull(message = "Currency required.")
    private Currency currency;
}
