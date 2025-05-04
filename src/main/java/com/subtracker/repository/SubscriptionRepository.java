package com.subtracker.repository;

import com.subtracker.model.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class SubscriptionRepository {

    private final ReactiveSubscriptionRepository reactiveSubscriptionRepository;

    @Autowired
    public SubscriptionRepository(ReactiveSubscriptionRepository reactiveSubscriptionRepository) {
        this.reactiveSubscriptionRepository = reactiveSubscriptionRepository;
    }

    public Subscription save(Subscription subscription) {
        try {
            return reactiveSubscriptionRepository.save(subscription).block(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to save subscription: {}", subscription, e);
            throw e;
        }
    }

    public Optional<Subscription> findById(String id) {
        try {
            return reactiveSubscriptionRepository.findById(id).blockOptional(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to find subscription ID: {}", id, e);
            throw e;
        }
    }

    public void deleteById(String id) {
        try {
            reactiveSubscriptionRepository.deleteById(id).block(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to find delete ID: {}", id, e);
            throw e;
        }
    }

    public List<Subscription> findAll() {
        try {
            return reactiveSubscriptionRepository.findAll()
                    .collectList()
                    .block(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to find all subscription", e);
            throw e;
        }
    }

    public List<Subscription> findAllById(List<String> ids) {
        try {
            return reactiveSubscriptionRepository.findAllById(ids)
                    .collectList()
                    .block(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to find all subscription", e);
            throw e;
        }
    }
}
