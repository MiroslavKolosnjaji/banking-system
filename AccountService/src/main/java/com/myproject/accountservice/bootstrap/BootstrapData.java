package com.myproject.accountservice.bootstrap;

import com.myproject.accountservice.dto.AccountDTO;
import com.myproject.accountservice.model.AccountType;
import com.myproject.accountservice.model.Currency;
import com.myproject.accountservice.model.Status;
import com.myproject.accountservice.repository.AccountRepository;
import com.myproject.accountservice.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * @author Miroslav Kološnjaji
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("dev")
public class BootstrapData implements CommandLineRunner {

    private final AccountService accountService;
    private final AccountRepository accountRepository;


    @Override
    public void run(String... args) throws Exception {

        if(accountRepository.count() == 0)
            loadAccountData();
    }

    private void loadAccountData() {

        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setBalance(new BigDecimal("0"));
        accountDTO.setAccountType(AccountType.CHECKING);
        accountDTO.setStatus(Status.ACTIVE);
        accountDTO.setCurrency(Currency.RSD);
        accountDTO.setUserId(1L);

        accountService.save(accountDTO);
    }
}
