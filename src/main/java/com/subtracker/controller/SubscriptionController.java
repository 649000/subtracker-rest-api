package com.subtracker.controller;


import com.subtracker.model.Subscription;
import com.subtracker.service.SubscriptionService;
import io.crnk.core.queryspec.QuerySpec;
import io.crnk.core.repository.ResourceRepositoryBase;
import io.crnk.core.resource.list.ResourceList;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.util.UUID;

@Component
@Slf4j
public class SubscriptionController extends ResourceRepositoryBase<Subscription, UUID> {

    private SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        super(Subscription.class);
        this.subscriptionService = subscriptionService;
    }

    @SneakyThrows
    @Override
    public Subscription create(@Valid Subscription subscription) {
        return subscriptionService.create(subscription);
    }

    @SneakyThrows
    @Override
    public ResourceList<Subscription> findAll(QuerySpec querySpec) {
        return querySpec.apply(subscriptionService.findAll());
    }

    @SneakyThrows
    @Override
    public Subscription findOne(@Valid UUID id, QuerySpec querySpec) {
        return subscriptionService.findOne(id);
    }

    @SneakyThrows
    @Override
    public Subscription save(@Valid Subscription subscription) {
        return subscriptionService.update(subscription);
    }

    @SneakyThrows
    @Override
    public void delete(@Valid UUID id) {
        subscriptionService.delete(id);
    }
}
