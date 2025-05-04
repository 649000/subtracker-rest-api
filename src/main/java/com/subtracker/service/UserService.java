package com.subtracker.service;

import com.subtracker.model.User;
import com.subtracker.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(Jwt jwt, User request) {
        User user = new User();
        user.setUid(jwt.getSubject());
        user.setEmail(jwt.getClaimAsString("email"));
//        TBD
//        user.setCountry(request.getCountry());
        return userRepository.save(user);
    }
}
