package com.subtracker.repository;

import com.subtracker.model.Subscription;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Blocking adapter for the ReactiveSubscriptionRepository.
 * This class intentially converts reactive operations into synchronous ones
 */
@Repository
@Slf4j
public class SubscriptionRepository {

    private static final Duration BLOCK_TIMEOUT = Duration.ofSeconds(5);

    private final ReactiveSubscriptionRepository reactiveSubscriptionRepository;

    @Autowired
    public SubscriptionRepository(ReactiveSubscriptionRepository reactiveSubscriptionRepository) {
        this.reactiveSubscriptionRepository = reactiveSubscriptionRepository;
    }

    /**
     * Saves a subscription synchronously.
     *
     * @param subscription the subscription to save
     * @return the saved subscription
     * @throws RuntimeException if the save operation fails
     */
    public Subscription save(Subscription subscription) {
        Objects.requireNonNull(subscription, "subscription must not be null");
        try {
            return reactiveSubscriptionRepository.save(subscription).block(BLOCK_TIMEOUT);
        } catch (RuntimeException e) {
            log.error("Failed to save subscription: {}", subscription, e);
            throw e;
        }
    }

    /**
     * Finds a subscription by its ID.
     *
     * @param id the subscription ID
     * @return an Optional containing the subscription if found
     * @throws RuntimeException if the operation fails
     */
    public Optional<Subscription> findById(String id) {
        Objects.requireNonNull(id, "id must not be null");
        try {
            return reactiveSubscriptionRepository.findById(id).blockOptional(BLOCK_TIMEOUT);
        } catch (RuntimeException e) {
            log.error("Failed to find subscription ID: {}", id, e);
            throw e;
        }
    }

    /**
     * Deletes a subscription by its ID.
     *
     * @param id the subscription ID
     * @throws RuntimeException if the deletion fails
     */
    public void deleteById(String id) {
        Objects.requireNonNull(id, "id must not be null");
        try {
            reactiveSubscriptionRepository.deleteById(id).block(BLOCK_TIMEOUT);
        } catch (RuntimeException e) {
            log.error("Failed to delete subscription ID: {}", id, e);
            throw e;
        }
    }


    /**
     * Retrieves all subscriptions.
     *
     * @return a list of all subscriptions
     * @throws RuntimeException if retrieval fails
     */
    public List<Subscription> findAll() {
        try {
            return reactiveSubscriptionRepository.findAll()
                    .collectList()
                    .block(BLOCK_TIMEOUT);
        } catch (RuntimeException e) {
            log.error("Failed to retrieve all subscriptions", e);
            throw e;
        }
    }

    /**
     * Retrieves all subscriptions by their IDs.
     *
     * @param ids the list of subscription IDs
     * @return a list of matching subscriptions
     * @throws RuntimeException if retrieval fails
     */
    public List<Subscription> findAllById(List<String> ids) {
        Objects.requireNonNull(ids, "ids must not be null");
        try {
            return reactiveSubscriptionRepository.findAllById(ids)
                    .collectList()
                    .block(BLOCK_TIMEOUT);
        } catch (RuntimeException e) {
            log.error("Failed to retrieve subscriptions by ID list", e);
            throw e;
        }
    }
}
