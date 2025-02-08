package com.myproject.accountservice.repository;

import com.myproject.accountservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * @author Miroslav Kološnjaji
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findById(Long id);
    Optional<Account> findAccountByAccountNumber(String accountNumber);
    List<Account> findAccountsByUserId(Long id);

}
