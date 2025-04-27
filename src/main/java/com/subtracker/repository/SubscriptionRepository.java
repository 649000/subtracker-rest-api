package com.subtracker.repository;

import com.subtracker.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
public class SubscriptionRepository {

    private final ReactiveSubscriptionRepository reactiveSubscriptionRepository;

    @Autowired
    public SubscriptionRepository(ReactiveSubscriptionRepository reactiveSubscriptionRepository) {
        this.reactiveSubscriptionRepository = reactiveSubscriptionRepository;
    }

    public Subscription save(Subscription subscription) {
        return reactiveSubscriptionRepository.save(subscription).block(Duration.ofSeconds(5));
    }

    public Optional<Subscription> findById(String id) {
        return reactiveSubscriptionRepository.findById(id).blockOptional(Duration.ofSeconds(5));
    }

    public void deleteById(String id) {
        reactiveSubscriptionRepository.deleteById(id).block(Duration.ofSeconds(5));
    }

    public List<Subscription> findAll() {
        return reactiveSubscriptionRepository.findAll()
                .collectList()
                .block(Duration.ofSeconds(5));
    }
}
