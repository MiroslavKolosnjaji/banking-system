package com.myproject.notificationservice.service.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Service
public class EmailStrategyFactory {

    private final TemplateEngine templateEngine;
    private Map<String, BaseEmailStrategy> strategies;

    public EmailStrategyFactory(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
        populateMap();
    }

    private void populateMap() {

        strategies = new HashMap<>();

        strategies.put("SUCCESSFUL", new SuccessfulTransactionEmailStrategy(templateEngine));
        strategies.put("FAILED", new FailedTransactionEmailStrategy(templateEngine));

    }

    public <T> Map<String, String> prepareEmailTemplate(T object) {

        for (BaseEmailStrategy strategy : strategies.values()) {
            if (strategy.supports(object.getClass(), object))
                return strategy.prepareEmailTemplate(object);
        }

        log.error("No suitable strategy found for object: {}", object.getClass());
        throw new IllegalArgumentException("No suitable strategy found.");
    }
}