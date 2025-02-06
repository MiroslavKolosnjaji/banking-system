package com.myproject.accountservice.controller.request;

import com.myproject.accountservice.model.Status;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusRequest {

    @NotNull(message = "Status should not be null")
    private Status status;
}
