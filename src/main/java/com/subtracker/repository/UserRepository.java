package com.subtracker.repository;

import com.subtracker.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepository {
    private final ReactiveUserRepository reactiveUserRepository;

    @Autowired
    public UserRepository(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }

    public Optional<User> findById(String userId) {
        try {
            return reactiveUserRepository.findById(userId).blockOptional(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to find user ID: {}", userId, e);
            throw e;
        }
    }

    public User save(User user) {
        try {
            return reactiveUserRepository.save(user).block(Duration.ofSeconds(5));
        } catch (RuntimeException e) {
            log.error("Failed to save user: {}", user, e);
            throw e;
        }
    }
}
