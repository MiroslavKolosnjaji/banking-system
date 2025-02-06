package com.myproject.accountservice.dto;

import com.myproject.accountservice.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatusDTO {

    private Status status;
}
