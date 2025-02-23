package com.myproject.notificationservice.service.strategy;

import com.myproject.notificationservice.dto.TransactionNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
public class FailedTransactionEmailStrategy extends BaseEmailStrategy {

    public final String EMAIL_TEMPLATE = "TransactionFailedEmailTemplate.html";

    public FailedTransactionEmailStrategy(TemplateEngine templateEngine) {
        super(templateEngine);
    }

    @Override
    public <T> Map<String, String> prepareEmailTemplate(T object) {


        if (!(object instanceof TransactionNotificationDTO transactionNotificationDTO))
            throw new IllegalArgumentException("Invalid instance provided.");

        String formattedDate = getFormattedDate(transactionNotificationDTO.getCreatedAt());

        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("transactionType", transactionNotificationDTO.getTransactionType());
        contextMap.put("details", transactionNotificationDTO.getDescription());
        contextMap.put("header", transactionNotificationDTO.getStatus());
        contextMap.put("date", formattedDate);

        Context context = getContext(contextMap);

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("email", transactionNotificationDTO.getEmail());
        resultMap.put("subject", transactionNotificationDTO.getTransactionType() + " - " + transactionNotificationDTO.getStatus());
        resultMap.put("htmlBody", templateEngine.process(EMAIL_TEMPLATE, context));

        return resultMap;

    }

    @Override
    public <T> boolean supports(Class<T> tClass, Object object) {

        if (TransactionNotificationDTO.class.isAssignableFrom(tClass)) {

            TransactionNotificationDTO transactionNotificationDTO = (TransactionNotificationDTO) object;

            return transactionNotificationDTO.getStatus().equals("FAILED");
        }

        return false;
    }
}
