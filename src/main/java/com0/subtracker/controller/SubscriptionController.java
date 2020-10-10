package com0.subtracker.controller;


import com0.subtracker.model.Subscription;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
@Slf4j
public class SubscriptionController extends ResourceRepositoryBase<Subscription, UUID> {
    public SubscriptionController() {
        super(Subscription.class);
    }

    @Override
    public ResourceList<Subscription> findAll(QuerySpec querySpec) {
        log.info("findAll");
        return null;
    }

    @Override
    public Subscription findOne(UUID id, QuerySpec querySpec) {
        log.info("findOne");
        return new Subscription();
    }
}
