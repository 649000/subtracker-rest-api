package com.subtracker.controller;

import com.subtracker.exception.SubscriptionException;
import com.subtracker.model.Subscription;
import com.subtracker.service.SubscriptionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

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
    public Subscription createSubscription(@RequestBody @Valid Subscription request,
                                           @AuthenticationPrincipal Jwt jwt) {
        log.debug("Creating subscription for user: {}, payload: {}", jwt.getSubject(), request);
        request.setUserId(jwt.getSubject());
        return subscriptionService.createSubscription(request);
    }

    @GetMapping
    public Iterable<Subscription> getSubscriptions(@AuthenticationPrincipal Jwt jwt) {
        log.debug("Fetching subscriptions for user: {}", jwt.getSubject());
        return subscriptionService.getSubscriptions(jwt.getSubject());
    }

    @GetMapping("/{subscriptionId}")
    public ResponseEntity<Subscription> getSubscription(@PathVariable String subscriptionId,
                                                        @AuthenticationPrincipal Jwt jwt) {
        try {
            Subscription subscription = subscriptionService.getSubscription(subscriptionId, jwt.getSubject());
            return ResponseEntity.ok(subscription);
        } catch (SecurityException e) {
            log.warn("Unauthorized access attempt by user {} for subscription {}", jwt.getSubject(), subscriptionId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (SubscriptionException e) {
            log.error("Subscription not found: {}", subscriptionId, e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            log.error("Unexpected error retrieving subscription: {}", subscriptionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{subscriptionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscription(@PathVariable String subscriptionId,
                                   @AuthenticationPrincipal Jwt jwt) {
        log.debug("Deleting subscription {} for user {}", subscriptionId, jwt.getSubject());
        subscriptionService.deleteSubscription(subscriptionId, jwt.getSubject());
    }

//    @PatchMapping("/{subscriptionId}")
//    public Subscription updateSubscription(@PathVariable("subscriptionId") String subscriptionId, @RequestBody Subscription request) {
//        log.debug("Subscription ID: {}", subscriptionId);
//        log.debug("Request Object: {}", request.toString());
//        try {
//            return subscriptionService.updateSubscription(subscriptionId, request);
//        } catch (ExecutionException e) {
//            throw new RuntimeException(e);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//    }
//


}
