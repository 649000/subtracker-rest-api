package com.subtracker.service;

import com.subtracker.model.User;
import com.subtracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private Jwt jwt;

    private UserService userService;

    @BeforeEach
    void setUp() {
        // Initialize mocks
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository);
    }

    @Test
    void testCreateUser_Success() {
        // Arrange
        String uid = "12345";
        String email = "test@subtracker.com";
        User request = new User();
        request.setCountry("SG");

        // Mock JWT behavior
        when(jwt.getSubject()).thenReturn(uid);
        when(jwt.getClaimAsString("email")).thenReturn(email);

        // Create a user object with the expected date values
        User savedUser = new User();
        savedUser.setUid(uid);
        savedUser.setEmail(email);
        savedUser.setCreatedDate(new Date());
        savedUser.setModifiedDate(new Date());
        savedUser.setSubscriptionList(new ArrayList<>());

        // Mock the behavior of the repository save
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(jwt, request);

        // Assert
        assertNotNull(result);
        assertEquals(uid, result.getUid());
        assertEquals(email, result.getEmail());
        assertNotNull(result.getCreatedDate());
        assertNotNull(result.getModifiedDate());
        assertTrue(result.getSubscriptionList().isEmpty());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_MissingUidOrEmail_ShouldThrowException() {
        // Arrange
        User request = new User();
        request.setCountry("SG");

        // Mock JWT to return null for uid and email
        when(jwt.getSubject()).thenReturn(null);
        when(jwt.getClaimAsString("email")).thenReturn(null);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(jwt, request);
        });

        assertEquals("Missing required JWT claims: subject or email", exception.getMessage());
        // Verify that the repository save was never called
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testGetUserByUid_Success() {
        // Arrange
        String uid = "12345";
        User user = new User();
        user.setUid(uid);
        when(userRepository.findById(uid)).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserByUid(uid);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(uid, result.get().getUid());
    }

    @Test
    void testGetUserByUid_NotFound() {
        // Arrange
        String uid = "12345";
        when(userRepository.findById(uid)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.getUserByUid(uid);

        // Assert
        assertFalse(result.isPresent());
    }
}

