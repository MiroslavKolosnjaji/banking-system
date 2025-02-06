package com.myproject.userservice.dto.account;

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
public class CreateAccountDTO {
    private Long userId;
    private String accountType;
    private String currency;
    private String status;

}
