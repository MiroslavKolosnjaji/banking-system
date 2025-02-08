package com.myproject.userservice.client.request;

import com.myproject.userservice.client.request.account.CreateAccountRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
public class RequestTest {

    @Test
    void testEquality_whenTwoObjectsAreEqual_thenCorrect() {

        CreateAccountRequest createAccountRequest1 = CreateAccountRequest.builder()
                .accountType("CHECKING")
                .build();

        CreateAccountRequest createAccountRequest2 = CreateAccountRequest.builder()
                .accountType("CHECKING")
                .build();

        assertAll("CreateAccountRequest",
                () -> assertEquals(createAccountRequest1, createAccountRequest2, "CreateAccountRequest objects should be equal."),
                () -> assertEquals(createAccountRequest1.hashCode(), createAccountRequest2.hashCode(), "CreateAccountRequest hashCode should be equals."));

    }
}
