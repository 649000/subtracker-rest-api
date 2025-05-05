package com.subtracker.service;

import com.subtracker.exception.SubscriptionException;
import com.subtracker.model.Subscription;
import com.subtracker.model.User;
import com.subtracker.repository.SubscriptionRepository;
import com.subtracker.repository.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

/**
 * Service class for managing user subscriptions.
 */
@Service
@Slf4j
public class SubscriptionService {


    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new subscription for a user and updates the user's subscription list.
     * TODO: Make transaction atomic
     * @param subscription The subscription to be created.
     * @return The saved subscription.
     * @throws IllegalArgumentException if the user does not exist.
     */
    public Subscription createSubscription(@NonNull Subscription subscription) {
        User user = userRepository.findById(subscription.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + subscription.getUserId()));

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        user.getSubscriptionList().add(savedSubscription.getSubscriptionId());
        userRepository.save(user);

        log.info("Created subscription {} for user {}", savedSubscription.getSubscriptionId(), user.getUid());
        return savedSubscription;
    }

    /**
     * Retrieves all subscriptions for a given user.
     *
     * @param userId The user ID.
     * @return A list of subscriptions belonging to the user.
     */
    public Iterable<Subscription> getSubscriptions(@NonNull final String userId) {
        return userRepository.findById(userId)
                .map(user -> subscriptionRepository.findAllById(user.getSubscriptionList()))
                .orElse(Collections.emptyList());
    }

    /**
     * Retrieves a subscription by its ID, validating ownership by the user.
     *
     * @param subscriptionId The subscription ID.
     * @param userId         The user ID.
     * @return The subscription if it exists and belongs to the user.
     * @throws SubscriptionException if the subscription does not exist or is not owned by the user.
     */
    public Subscription getSubscription(@NonNull final String subscriptionId, @NonNull final String userId) throws SubscriptionException {
        return subscriptionRepository.findById(subscriptionId)
                .filter(subscription -> userId.equals(subscription.getUserId()))
                .orElseThrow(() -> new SubscriptionException("Subscription does not exist or does not belong to user"));
    }

    /**
     * Deletes a subscription and removes it from the user's subscription list.
     * TODO: Make transaction atomic
     * @param subscriptionId The subscription ID.
     * @param userId         The user ID.
     * @throws SecurityException if the subscription does not exist or is not owned by the user.
     */
    public void deleteSubscription(@NonNull final String subscriptionId, @NonNull final String userId) {
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .filter(sub -> userId.equals(sub.getUserId()))
                .orElseThrow(() -> new SecurityException("Subscription does not exist or does not belong to user"));

        subscriptionRepository.deleteById(subscriptionId);

        userRepository.findById(userId).ifPresent(user -> {
            user.getSubscriptionList().remove(subscriptionId);
            userRepository.save(user);
        });

        log.info("Deleted subscription {} for user {}", subscriptionId, userId);
    }
}
