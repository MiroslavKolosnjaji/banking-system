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
public class SuccessfulTransactionEmailStrategy extends BaseEmailStrategy {

    public final String EMAIL_TEMPLATE = "TransactionEmailTemplate.html";

    public SuccessfulTransactionEmailStrategy(TemplateEngine templateEngine) {
        super(templateEngine);
    }


    @Override
    public <T> Map<String, String> prepareEmailTemplate(T object) {

        if (!(object instanceof TransactionNotificationDTO transactionNotificationDTO))
            throw new IllegalArgumentException("Invalid instance provided.");

        String formattedDate = getFormattedDate(transactionNotificationDTO.getCreatedAt());

        Map<String, Object> contextMap = new HashMap<>();

        contextMap.put("accountNumber", transactionNotificationDTO.getAccountNumber());
        contextMap.put("availableFunds", formatCurrency(transactionNotificationDTO.getCurrency(), transactionNotificationDTO.getBalance()));
        contextMap.put("header", transactionNotificationDTO.getTransactionType());
        contextMap.put("amount", formatCurrency(transactionNotificationDTO.getCurrency(), transactionNotificationDTO.getAmount()));
        contextMap.put("date", formattedDate);

        String moneyFlowType = transactionNotificationDTO.getTransactionType().equals("DEPOSIT") ?   "Inflow" :  "Outflow";

        contextMap.put("moneyFlowType", moneyFlowType);

        Context context = getContext(contextMap);


        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("email", transactionNotificationDTO.getEmail());
        resultMap.put("subject", transactionNotificationDTO.getTransactionType());
        resultMap.put("htmlBody", templateEngine.process(EMAIL_TEMPLATE, context));

        return resultMap;

    }

    @Override
    public <T> boolean supports(Class<T> tClass, Object object) {

        if (TransactionNotificationDTO.class.isAssignableFrom(tClass)) {

            TransactionNotificationDTO transactionNotificationDTO = (TransactionNotificationDTO) object;

            return transactionNotificationDTO.getStatus().equals("SUCCESS");
        }

        return false;
    }

}
