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

import java.util.Collections;
import java.util.Optional;

@Service
@Slf4j
public class SubscriptionService {


    private SubscriptionRepository subscriptionRepository;
    private UserRepository userRepository;

    @Autowired
    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    //TODO: Atomic Transaction
    public Subscription createSubscription(@NonNull Subscription subscription) {
        User user = userRepository.findById(subscription.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + subscription.getUserId()));

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        user.getSubscriptionList().add(savedSubscription.getSubscriptionId());
        userRepository.save(user);

        return savedSubscription;
    }

    public Iterable<Subscription> getSubscriptions(@NonNull final String userId) {
        return userRepository.findById(userId)
                .map(user -> subscriptionRepository.findAllById(user.getSubscriptionList()))
                .orElse(Collections.emptyList());
    }

    public Subscription getSubscription(@NonNull final String subscriptionId, @NonNull final String userId) throws SubscriptionException {
        return subscriptionRepository.findById(subscriptionId)
                .filter(subscription -> userId.equals(subscription.getUserId()))
                .orElseThrow(() -> new SubscriptionException("Subscription does not exist or does not belong to user"));
    }

    //TODO: Atomic Transaction
    public void deleteSubscription(@NonNull final String subscriptionId, @NonNull final String userId) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findById(subscriptionId);

        if (subscriptionOpt.isEmpty() || !userId.equals(subscriptionOpt.get().getUserId())) {
            throw new SecurityException("Subscription does not exist or does not belong to user");
        }

        subscriptionRepository.deleteById(subscriptionId);

        userRepository.findById(userId).ifPresent(user -> {
            user.getSubscriptionList().remove(subscriptionId);
            userRepository.save(user);
        });
    }
}
