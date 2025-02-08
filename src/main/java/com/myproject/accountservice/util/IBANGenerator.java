package com.myproject.accountservice.util;

import java.util.Random;

/**
 * @author Miroslav Kološnjaji
 */
public class IBANGenerator {

    private static final String COUNTRY = "RS";

    public static String generateIBAN() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append(COUNTRY);

        for (int i = 0; i < 5; i++) {
            sb.append(random.nextInt(1000, 9999));
        }
       return sb.toString();
    }
}