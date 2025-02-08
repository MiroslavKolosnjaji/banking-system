package com.myproject.userservice.client.response;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Miroslav Kološnjaji
 */
public class ResponseTest {

    @Test
    void testEquality_whenTwoObjectsAreEqual_thenCorrect() {

        AccountResponse accountResponse1 = new AccountResponse();
        accountResponse1.setUserId(1L);
        accountResponse1.setStatus("ACTIVE");

        AccountResponse accountResponse2 = new AccountResponse();
        accountResponse2.setUserId(1L);
        accountResponse2.setStatus("ACTIVE");

        ErrorResponse errorResponse1 = new ErrorResponse();
        errorResponse1.setMessage("TEST");
        errorResponse1.setTimestamp("123");

        ErrorResponse errorResponse2 = new ErrorResponse();
        errorResponse2.setMessage("TEST");
        errorResponse2.setTimestamp("123");


        assertAll("AccountResponse",
                () -> assertEquals(accountResponse1, accountResponse2, "AccountResponse objects should be equal."),
                () -> assertEquals(accountResponse1.hashCode(), accountResponse2.hashCode(), "AccountResponse hashCode should be equals"));

        assertAll("ErrorResponse",
                () -> assertEquals(errorResponse1, errorResponse2, "ErrorResponse objects should be equal."),
                () -> assertEquals(errorResponse1.hashCode(), errorResponse2.hashCode(), "ErrorResponse hashCode should be equals."));

    }
}
