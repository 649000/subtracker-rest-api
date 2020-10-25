package com.subtracker.controller;

import com.subtracker.model.User;
import com.subtracker.service.UserService;
import io.crnk.core.exception.MethodNotAllowedException;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;

@Component
@Slf4j
public class UserController extends ResourceRepositoryBase<User, String> {

    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super(User.class);
        this.userService = userService;
    }

    @SneakyThrows
    @Override
    public User create(@Valid User user) {
        return userService.create(user);
    }

    @Override
    public ResourceList<User> findAll(QuerySpec querySpec) {
        throw new MethodNotAllowedException("Method not allowed");
    }

    @SneakyThrows
    @Override
    public User findOne(@Valid String id, QuerySpec querySpec) {
        return userService.findOne(id);
    }

    @SneakyThrows
    @Override
    public User save(@Valid User user) {
        return userService.update(user);
    }

    @SneakyThrows
    @Override
    public void delete(@Valid String id) {
        userService.delete(id);
    }
}
