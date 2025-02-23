package com.myproject.userservice.service;

import com.myproject.userservice.dto.user.UserDTO;
import com.myproject.userservice.exception.service.EmailNotFoundException;
import com.myproject.userservice.exception.service.UserNotFoundException;


/**
 * @author Miroslav Kološnjaji
 */
public interface UserService extends BaseCRUD<UserDTO, Long> {

    String getUserEmail(Long id) throws UserNotFoundException, EmailNotFoundException;
}
