package com.myproject.notificationservice.service.strategy;

import com.myproject.notificationservice.util.CurrencyUtils;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;


/**
 * @author Miroslav Kološnjaji
 */
@RequiredArgsConstructor
public abstract class BaseEmailStrategy {

    protected final TemplateEngine templateEngine;

    public abstract <T> Map<String, String> prepareEmailTemplate(T object);

    public abstract <T> boolean supports(Class<T> tClass, Object object);

    protected Context getContext(Map<String, Object> contextMap){

        Context context = new Context();
        context.setVariables(contextMap);

        return context;
    }


    protected String getFormattedDate(Instant instant) {

        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy, HH:mm:ss");

        return localDateTime.format(formatter);
    }

    protected String formatCurrency(String currency, BigDecimal value) {
        return CurrencyUtils.formatCurrency(value, currency);
    }

}
