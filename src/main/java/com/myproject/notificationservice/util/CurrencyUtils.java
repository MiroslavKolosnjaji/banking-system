package com.myproject.notificationservice.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * @author Miroslav Kološnjaji
 */
public class CurrencyUtils {

    private static final Map<String, Locale> currencyLocales = new HashMap<>();

    static {
        currencyLocales.put("RSD", new Locale("sr", "RS"));
        currencyLocales.put("EUR", Locale.GERMANY);
        currencyLocales.put("USD", Locale.US);
        currencyLocales.put("GBP", Locale.UK);
        currencyLocales.put("CHF", new Locale("de", "CH"));
    }


    public static String formatCurrency(BigDecimal amount, String currency){
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setCurrency(Currency.getInstance(currency));
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);

        if(Objects.equals(currency, "RSD"))
            return numberFormat.format(amount) + " RSD";

        return numberFormat.format(amount);
    }
}
