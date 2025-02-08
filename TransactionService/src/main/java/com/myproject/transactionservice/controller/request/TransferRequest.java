package com.myproject.transactionservice.controller.request;

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
public class TransferRequest extends BaseRequest {

    @NotEmpty(message = "Recipient account number required.")
    @Size(min = 14, max = 34, message = "Recipient account number length must be between 15 and 34 characters.")
    private String recipientAccountNumber;

}
