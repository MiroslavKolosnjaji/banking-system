package com.myproject.accountservice.exception.controller.handler;

import com.myproject.accountservice.dto.AccountDTO;
import com.myproject.accountservice.exception.controller.AccountStatusViolationException;
import com.myproject.accountservice.exception.controller.EntityNotFoundException;
import com.myproject.accountservice.exception.service.UnauthorizedAccountAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Miroslav Kološnjaji
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedAccountAccessException.class)
    public ResponseEntity<Object> handleUnauthorizedAccountAccessException(UnauthorizedAccountAccessException e){
        return  new ResponseEntity<>(createErrorBody(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccountStatusViolationException.class)
    public ResponseEntity<Object> handleAccountStatusViolationException(AccountStatusViolationException e){
        return new ResponseEntity<>(createErrorBody(e.getMessage()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e){
        return new ResponseEntity<>(createErrorBody(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    private Map<String, Object> createErrorBody(String message){
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", message);
        return body;
    }
}
