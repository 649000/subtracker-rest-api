package com.subtracker.repository;

import com.google.cloud.spring.data.firestore.FirestoreReactiveRepository;
import com.subtracker.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface ReactiveUserRepository extends FirestoreReactiveRepository<User> {
}