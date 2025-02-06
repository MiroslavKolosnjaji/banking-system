package com.myproject.userservice.controller;

import com.myproject.userservice.dto.user.UserDTO;
import com.myproject.userservice.exception.controller.EntityAlreadyExistsException;
import com.myproject.userservice.exception.controller.EntityNotFoundException;
import com.myproject.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private static final String PATH_VARIABLE = "userId";
    public static final String USER_URI = "/api/v1/user";
    public static final String USER_ID = "/{userId}";
    public static final String USER_URI_WITH_ID = USER_URI + USER_ID;
    public static final String USER_URI_CHECK_USER = USER_URI + "/check" + USER_ID;


    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserDTO> save(@Valid @RequestBody UserDTO userDTO) throws EntityAlreadyExistsException {

        UserDTO savedUser = userService.save(userDTO);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(HttpHeaders.LOCATION, USER_URI + "/" + savedUser.getId());

        return new ResponseEntity<>(savedUser, httpHeaders, HttpStatus.CREATED);
    }

    @PutMapping(USER_ID)
    public ResponseEntity<UserDTO> update(@PathVariable(PATH_VARIABLE) Long userId, @Valid @RequestBody UserDTO userDTO) throws EntityNotFoundException {

        UserDTO updatedUser = userService.update(userId, userDTO);
        return new ResponseEntity<>(updatedUser, HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {

        List<UserDTO> userDTOList = userService.getAll();
        return new ResponseEntity<>(userDTOList, HttpStatus.OK);
    }

    @GetMapping(USER_ID)
    public ResponseEntity<UserDTO> getUser(@PathVariable(PATH_VARIABLE) Long userId) throws EntityNotFoundException {

        UserDTO userDTO = userService.getById(userId);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @DeleteMapping(USER_ID)
    public ResponseEntity<Void> deleteUser(@PathVariable(PATH_VARIABLE) Long userId) throws EntityNotFoundException {
        userService.deleteById(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/check" + USER_ID)
    public ResponseEntity<Boolean> isUserExists(@PathVariable(PATH_VARIABLE) Long userId) throws EntityNotFoundException {
        return new ResponseEntity<>(userService.checkIfUserExists(userId), HttpStatus.OK);
    }

}
