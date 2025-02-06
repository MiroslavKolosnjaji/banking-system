package com.myproject.userservice.client.response;

import lombok.Data;

/**
 * @author Miroslav Kološnjaji
 */
@Data
public class ErrorResponse {

    private String message;
    private String timestamp;
}
