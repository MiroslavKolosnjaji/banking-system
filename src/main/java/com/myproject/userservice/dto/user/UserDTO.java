package com.myproject.userservice.dto.user;

import com.myproject.userservice.dto.account.AccountDTO;
import com.myproject.userservice.dto.role.RoleDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Miroslav Kološnjaji
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {

    private Long id;

    @NotEmpty(message = "Username cannot be empty")
    private String username;

    @NotEmpty(message = "Password cannot be empty")
    private String password;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    private String email;

    @NotEmpty(message = "First name cannot be empty")
    private String firstname;

    @NotEmpty(message = "Last name cannot be empty")
    private String lastname;

    @Singular
    private Set<String> roles;

    private List<AccountDTO> bankAccounts;

    @Getter(AccessLevel.NONE)
    private AccountDTO account;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    public void setAccount(AccountDTO accountDTO){
        if(this.bankAccounts == null)
            this.bankAccounts = new ArrayList<>();

        bankAccounts.add(accountDTO);
    }
}
