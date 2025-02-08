package com.myproject.transactionservice.client.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author Miroslav Kološnjaji
 */
@Component
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AccountClientProperties {

    @Value("${account.service.url}")
    private String url;
}
