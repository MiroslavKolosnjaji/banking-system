package com.myproject.accountservice.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Miroslav Kološnjaji
 */
class IBANGeneratorTest {

    @Test
    void testIBANGenerator_callMethod_returnsRandomGeneratedString() {

        //given
        IBANGenerator generator = new IBANGenerator();

        //given && when
        String iban = generator.generateIBAN();

        assertNotNull(iban, "Iban should not be null");
        assertTrue(iban.contains("RS"), "Iban should contain 'RS' initials");
        assertEquals(22, iban.length(), "Iban length should be 22");
        System.out.printf(iban);
    }
}