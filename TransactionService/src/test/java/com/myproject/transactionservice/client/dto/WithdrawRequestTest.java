package com.myproject.transactionservice.client.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
class WithdrawRequestTest {

    @DisplayName("Withdraw request - object equality")
    @Test
    void testEquality_whenTwoObjectsAreEqual_thenCorrect(){

        WithdrawRequestDTO withdrawRequest = new WithdrawRequestDTO();
        withdrawRequest.setUserId(1L);
        withdrawRequest.setAmount(new BigDecimal("100"));

        WithdrawRequestDTO withdrawRequest2 = new WithdrawRequestDTO();
        withdrawRequest2.setUserId(1L);
        withdrawRequest2.setAmount(new BigDecimal("100"));

        int hashCode1 = withdrawRequest.hashCode();
        int hashCode2 = withdrawRequest2.hashCode();

        assertEquals(withdrawRequest, withdrawRequest2, "WitdrawRequest objects should be equal");
        assertEquals(hashCode1, hashCode2, "Hashcode doesn't match.");

    }

}