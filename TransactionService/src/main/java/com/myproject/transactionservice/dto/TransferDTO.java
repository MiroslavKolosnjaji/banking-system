package com.myproject.transactionservice.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * @author Miroslav Kološnjaji
 */
@Getter
@Setter
@SuperBuilder
public class TransferDTO extends BaseDTO {

    private String recipientAccountNumber;
}
