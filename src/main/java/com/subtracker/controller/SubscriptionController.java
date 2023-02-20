package com.subtracker.controller;

import com.subtracker.model.Subscription;
import com.subtracker.service.SubscriptionService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/subscription")
@Slf4j
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @Autowired
    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Subscription createSubscription(@RequestBody Subscription request) {
        log.debug("Request Object: {}", request.toString());
        try {
            return subscriptionService.createSubscription(request);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping
    public Iterable<Subscription> getSubscriptions() throws ExecutionException, InterruptedException {
        return subscriptionService.getSubscriptions();
    }

    @GetMapping("/{subscriptionId}")
    public Subscription getSubscription(@PathVariable final String subscriptionId) {
        try {
            return subscriptionService.getSubscription(subscriptionId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @PatchMapping("/{subscriptionId}")
    public Subscription updateSubscription(@PathVariable("subscriptionId") String subscriptionId, @RequestBody Subscription request) {
        log.debug("Subscription ID: {}", subscriptionId);
        log.debug("Request Object: {}", request.toString());
        try {
            return subscriptionService.updateSubscription(subscriptionId, request);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@PathVariable String subscriptionId) {
        log.debug("Subscription ID: {}", subscriptionId);
        subscriptionService.deleteSubscription(subscriptionId);
    }

}
