package com.subtracker.repository;

import com.subtracker.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * Adapter for using the ReactiveUserRepository in blocking (synchronous) code paths.
 * Intended only for use in synchronous contexts.
 */
@Repository
@Slf4j
public class UserRepository {

    private static final Duration BLOCK_TIMEOUT = Duration.ofSeconds(5);

    private final ReactiveUserRepository reactiveUserRepository;

    @Autowired
    public UserRepository(ReactiveUserRepository reactiveUserRepository) {
        this.reactiveUserRepository = reactiveUserRepository;
    }

    /**
     * Finds a user by ID using a blocking call on the reactive repository.
     *
     * @param userId the ID of the user
     * @return an Optional containing the User, or empty if not found
     */
    public Optional<User> findById(String userId) {
        try {
            return reactiveUserRepository.findById(userId)
                    .blockOptional(BLOCK_TIMEOUT);
        } catch (Exception e) {
            log.error("Failed to find user with ID {}: {}", userId, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Saves the user entity using a blocking call on the reactive repository.
     *
     * @param user the User to save
     * @return the saved User
     */
    public User save(User user) {
        try {
            User saved = reactiveUserRepository.save(user)
                    .block(BLOCK_TIMEOUT);
            if (saved == null) {
                throw new IllegalStateException("Saved user is null");
            }
            return saved;
        } catch (Exception e) {
            log.error("Failed to save user with ID {}: {}", user.getUid(), e.getMessage(), e);
            throw e;
        }
    }
}
