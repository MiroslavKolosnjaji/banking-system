package com.myproject.userservice.client.account;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Miroslav Kološnjaji
 */
@Component
@ConfigurationProperties(prefix = "account.service")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountClientProperties {
    private String url;
}
