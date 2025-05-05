package com.subtracker.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.subtracker.model.User;
import org.springframework.stereotype.Repository;

/**
 * Reactive Firestore repository for User documents.
 * Provides asynchronous access to user data.
 */
@Repository
public interface ReactiveUserRepository extends FirestoreReactiveRepository<User> {
}