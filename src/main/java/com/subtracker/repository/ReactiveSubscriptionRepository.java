package com.subtracker.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.subtracker.model.Subscription;
import org.springframework.stereotype.Repository;

/**
 * Reactive Firestore repository for Subscription documents.
 * Provides asynchronous access to subscription data.
 */
@Repository
public interface ReactiveSubscriptionRepository extends FirestoreReactiveRepository<Subscription> {
}