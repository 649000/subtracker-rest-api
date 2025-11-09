package com.subtracker.controller;

import com.subtracker.exception.SubscriptionException;
import com.subtracker.model.Subscription;
import com.subtracker.service.SubscriptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubscriptionControllerTest {

    @Mock
    private SubscriptionService subscriptionService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private SubscriptionController subscriptionController;

    private Subscription sampleSubscription;
    private final String userId = "test-user-id";
    private final String subscriptionId = "test-subscription-id";

    @BeforeEach
    void setUp() {
        sampleSubscription = new Subscription();
        sampleSubscription.setSubscriptionId(subscriptionId);
        sampleSubscription.setUserId(userId);
        sampleSubscription.setName("Test Subscription");
        
        when(jwt.getSubject()).thenReturn(userId);
    }

    @Test
    void createSubscription_shouldReturnCreatedSubscription() {
        Subscription request = new Subscription();
        request.setName("New Subscription");
        
        when(subscriptionService.createSubscription(any(Subscription.class))).thenReturn(sampleSubscription);

        Subscription result = subscriptionController.createSubscription(request, jwt);

        assertEquals(sampleSubscription, result);
        assertEquals(userId, request.getUserId());
        verify(subscriptionService).createSubscription(request);
    }

    @Test
    void getSubscriptions_shouldReturnUserSubscriptions() {
        List<Subscription> subscriptions = Arrays.asList(sampleSubscription);
        when(subscriptionService.getSubscriptions(userId)).thenReturn(subscriptions);

        Iterable<Subscription> result = subscriptionController.getSubscriptions(jwt);

        assertEquals(subscriptions, result);
        verify(subscriptionService).getSubscriptions(userId);
    }

    @Test
    void getSubscription_shouldReturnOkWhenSuccessful() throws SubscriptionException {
        when(subscriptionService.getSubscription(subscriptionId, userId)).thenReturn(sampleSubscription);

        ResponseEntity<Subscription> response = subscriptionController.getSubscription(subscriptionId, jwt);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleSubscription, response.getBody());
        verify(subscriptionService).getSubscription(subscriptionId, userId);
    }

    @Test
    void getSubscription_shouldReturnForbiddenWhenSecurityException() throws SubscriptionException {
        when(subscriptionService.getSubscription(subscriptionId, userId))
                .thenThrow(new SecurityException("Unauthorized"));

        ResponseEntity<Subscription> response = subscriptionController.getSubscription(subscriptionId, jwt);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertNull(response.getBody());
        verify(subscriptionService).getSubscription(subscriptionId, userId);
    }

    @Test
    void getSubscription_shouldReturnNotFoundWhenSubscriptionException() throws SubscriptionException {
        when(subscriptionService.getSubscription(subscriptionId, userId))
                .thenThrow(new SubscriptionException("Not found"));

        ResponseEntity<Subscription> response = subscriptionController.getSubscription(subscriptionId, jwt);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(subscriptionService).getSubscription(subscriptionId, userId);
    }

    @Test
    void getSubscription_shouldReturnInternalServerErrorWhenUnexpectedException() throws SubscriptionException {
        when(subscriptionService.getSubscription(subscriptionId, userId))
                .thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<Subscription> response = subscriptionController.getSubscription(subscriptionId, jwt);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
        verify(subscriptionService).getSubscription(subscriptionId, userId);
    }

    @Test
    void deleteSubscription_shouldCallServiceAndReturnNoContent() {
        assertDoesNotThrow(() -> {
            subscriptionController.deleteSubscription(subscriptionId, jwt);
        });
        
        verify(subscriptionService).deleteSubscription(subscriptionId, userId);
    }
}
