package com.myproject.userservice.service;

import com.myproject.userservice.exception.controller.EntityAlreadyExistsException;
import com.myproject.userservice.exception.controller.EntityNotFoundException;

import java.util.List;

/**
 * @author Miroslav Kološnjaji
 */
public interface BaseCRUD<T, ID> {

     T save(T type) throws EntityAlreadyExistsException;
     T update(ID id, T type) throws EntityNotFoundException;
     List<T> getAll();
     T getById(ID id) throws EntityNotFoundException;
     void deleteById(ID id) throws EntityNotFoundException;
}
