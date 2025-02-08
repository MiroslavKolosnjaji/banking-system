package com.myproject.userservice.mapper;

import com.myproject.userservice.dto.account.AccountDTO;
import com.myproject.userservice.dto.account.CreateAccountDTO;
import com.myproject.userservice.client.request.account.CreateAccountRequest;
import com.myproject.userservice.client.response.AccountResponse;
import org.mapstruct.Mapper;

/**
 * @author Miroslav Kološnjaji
 */
@Mapper
public interface AccountMapper {


    CreateAccountRequest createAccountDTOToCreateAccountRequest(CreateAccountDTO createAccountDTO);

    AccountDTO accountResponseToAccountDTO(AccountResponse accountResponse);
}
