package com.subtracker.controller;

import com.subtracker.model.User;
import com.subtracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private String testUserId = "test-user-id";

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUid(testUserId);
        testUser.setEmail("test@example.com");
    }

    @Test
    void createUser_shouldReturnCreatedUser_whenSuccessful() {
        // Given
        when(jwt.getSubject()).thenReturn(testUserId);
        when(userService.createUser(any(Jwt.class), any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<User> response = userController.createUser(testUser, jwt);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(testUser, response.getBody());
        verify(userService, times(1)).createUser(jwt, testUser);
    }

    @Test
    void createUser_shouldReturnInternalServerError_whenUserServiceThrowsException() {
        // Given
        when(jwt.getSubject()).thenReturn(testUserId);
        when(userService.createUser(any(Jwt.class), any(User.class))).thenThrow(new RuntimeException("Test exception"));

        // When
        ResponseEntity<User> response = userController.createUser(testUser, jwt);

        // Then
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService, times(1)).createUser(jwt, testUser);
    }

    @Test
    void createUser_shouldLogJwtSubject_whenCreatingUser() {
        // Given
        when(jwt.getSubject()).thenReturn(testUserId);
        when(userService.createUser(any(Jwt.class), any(User.class))).thenReturn(testUser);

        // When
        userController.createUser(testUser, jwt);

        // Then
        verify(jwt, times(1)).getSubject();
    }
}
