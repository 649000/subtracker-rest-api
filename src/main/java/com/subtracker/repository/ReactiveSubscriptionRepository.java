package com.subtracker.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.subtracker.model.Subscription;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveSubscriptionRepository extends FirestoreReactiveRepository<Subscription> {
}