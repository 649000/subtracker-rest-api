package com.subtracker.service;

import com.subtracker.model.User;
import com.subtracker.repository.UserRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

/**
 * Service class for handling user-related operations such as user creation.
 */
@Data
@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user based on the provided JWT and user request data.
     *
     * @param jwt the JWT token containing user data
     * @param request the user object containing additional information
     * @return the saved user object
     * @throws IllegalArgumentException if required JWT claims (uid or email) are missing
     */
    public User createUser(Jwt jwt, User request) {

        String uid = jwt.getSubject();
        String email = jwt.getClaimAsString("email");

        if (uid == null || email == null) {
            log.warn("JWT is missing required claims: uid or email");
            throw new IllegalArgumentException("Missing required JWT claims: subject or email");
        }

        User user = new User();
        user.setUid(uid);
        user.setEmail(email);
        user.setCreatedDate(new Date());
        user.setModifiedDate(new Date());
        user.setSubscriptionList(new ArrayList<>());

        User savedUser = userRepository.save(user);
        log.info("User created successfully with UID: {}", savedUser.getUid());
        return savedUser;
    }

    /**
     * Retrieves user by given uid
     * @param uid user's uid
     * @return User object
     */
    public Optional<User> getUserByUid(String uid) {
        return userRepository.findById(uid);
    }
}
