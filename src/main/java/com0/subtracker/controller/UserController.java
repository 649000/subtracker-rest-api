package com0.subtracker.controller;

import com0.subtracker.model.User;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class UserController extends ResourceRepositoryBase<User, UUID> {

    public UserController() {
        super(User.class);
    }

    @Override
    public ResourceList<User> findAll(QuerySpec querySpec) {
        log.info("findAll");
        return null;
    }
}
