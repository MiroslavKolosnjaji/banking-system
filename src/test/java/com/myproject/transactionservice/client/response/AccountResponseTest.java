package com.myproject.transactionservice.client.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
class AccountResponseTest {

    @DisplayName("Account Response - object equality")
    @Test
    void testEquality_whenTwoObjectsAreEqual_thenCorrect(){

        AccountResponse accountResponse1 = AccountResponse.builder()
                .accountNumber("123")
                .balance(new BigDecimal("123"))
                .status("test")
                .description("test")
                .amount(new BigDecimal("123"))
                .build();


        AccountResponse accountResponse2 = AccountResponse.builder()
                .accountNumber("123")
                .balance(new BigDecimal("123"))
                .status("test")
                .description("test")
                .amount(new BigDecimal("123"))
                .build();

        int hashCode1 = accountResponse1.hashCode();
        int hashCode2 = accountResponse2.hashCode();

        assertEquals(accountResponse1, accountResponse2, "AccountResponse objects should be equals.");
        assertEquals(hashCode1, hashCode2, "Hashcode doesn't match.");



    }
}