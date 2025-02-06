package com.myproject.transactionservice.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
class DepositRequestTest {


    @DisplayName("Deposit request - object equality")
    @Test
    void testEquality_whenTwoObjectsAreEqual_thenCorrect() {

        DepositRequestDTO depositRequest = new DepositRequestDTO();
        depositRequest.setUserId(1L);
        depositRequest.setAmount(new BigDecimal("100"));

        DepositRequestDTO depositRequest2 = new DepositRequestDTO();
        depositRequest2.setUserId(1L);
        depositRequest2.setAmount(new BigDecimal("100"));

        int hashCode1 = depositRequest.hashCode();
        int hashCode2 = depositRequest2.hashCode();


        assertEquals(depositRequest, depositRequest2, "DepositRequest objects should be equal.");
        assertEquals(hashCode1, hashCode2, "Hashcode doesn't match.");
    }
}