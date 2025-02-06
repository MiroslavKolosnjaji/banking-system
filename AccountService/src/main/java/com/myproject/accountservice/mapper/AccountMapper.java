package com.myproject.accountservice.mapper;

import com.myproject.accountservice.controller.request.UpdateAccountRequest;
import com.myproject.accountservice.dto.*;
import com.myproject.accountservice.controller.request.AccountRequest;
import com.myproject.accountservice.controller.request.AccountStatusRequest;
import com.myproject.accountservice.controller.response.AccountResponse;
import com.myproject.accountservice.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper
public interface AccountMapper {

    AccountDTO accountToAccountDTO(Account account);
    Account accountDTOToAccount(AccountDTO accountDTO);

    AccountResponse accountDTOToAccountResponse(AccountDTO accountDTO);
    AccountDTO accountResponseToAccountDTO(AccountResponse accountResponse);

    AccountRequest accountDTOToAccountRequest(AccountDTO accountDTO);

    @Mappings({@Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "accountNumber", ignore = true),
            @Mapping(target = "balance", ignore = true),
            @Mapping(target = "status", ignore = true)
    })
    AccountDTO accountRequestToAccountDTO(AccountRequest accountRequest);



    @Mappings({@Mapping(target = "balance", ignore = true),
            @Mapping(target = "createdAt", ignore = true),
            @Mapping(target = "updatedAt", ignore = true),
            @Mapping(target = "currency", ignore = true)
    })
    AccountDTO updateAccountRequestToAccountDTO(UpdateAccountRequest updateAccountRequest);

    AccountStatusDTO accountStatusRequestToAccountStatusDTO(AccountStatusRequest accountStatusRequest);

    AccountDetailsDTO accountToAccountDetails(Account account);

    @Mapping(target = "accountType", ignore = true)
    AccountResponse accountDetailsDTOToAccountResponse(AccountDetailsDTO accountDetailsDTO);
}
