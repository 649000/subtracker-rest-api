package com.subtracker.service;

import com.subtracker.exception.SubscriptionException;
import com.subtracker.model.Subscription;
import com.subtracker.model.User;
import com.subtracker.repository.SubscriptionRepository;
import com.subtracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void createSubscription_shouldSaveSubscriptionAndUpdateUser() {
        // Arrange
        String userId = "user123";
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId("sub1");
        subscription.setUserId(userId);
        Subscription savedSubscription = new Subscription();
        savedSubscription.setSubscriptionId("sub1");
        savedSubscription.setUserId(userId);
        User user = new User();
        user.setUid(userId);
        user.setSubscriptionList(new ArrayList<>());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.save(subscription)).thenReturn(savedSubscription);
        when(userRepository.save(user)).thenReturn(user);

        // Act
        Subscription result = subscriptionService.createSubscription(subscription);

        // Assert
        assertEquals(savedSubscription, result);
        assertTrue(user.getSubscriptionList().contains("sub1"));
        verify(subscriptionRepository).save(subscription);
        verify(userRepository).save(user);
    }

    @Test
    void getSubscriptions_shouldReturnSubscriptionsForUser() {
        String userId = "user123";
        List<String> subIds = List.of("sub1", "sub2");

        User user = new User();
        user.setUid(userId);
        user.setSubscriptionList(new ArrayList<>(subIds));

        Subscription sub1 = new Subscription();
        sub1.setSubscriptionId("sub1");
        sub1.setUserId(userId);

        Subscription sub2 = new Subscription();
        sub2.setSubscriptionId("sub2");
        sub2.setUserId(userId);

        List<Subscription> expected = List.of(sub1, sub2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(subscriptionRepository.findAllById(subIds)).thenReturn(expected);

        Iterable<Subscription> result = subscriptionService.getSubscriptions(userId);

        assertIterableEquals(expected, result);
    }

    @Test
    void getSubscription_shouldReturnIfExistsAndOwnedByUser() throws SubscriptionException {
        String userId = "user123";
        String subId = "sub1";
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subId);
        subscription.setUserId(userId);
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));

        Subscription result = subscriptionService.getSubscription(subId, userId);

        assertEquals(subscription, result);
    }

    @Test
    void deleteSubscription_shouldRemoveSubscriptionFromUser() {
        String userId = "user123";
        String subId = "sub1";
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subId);
        subscription.setUserId(userId);
        User user = new User();
        user.setUid(userId);
        user.setSubscriptionList(new ArrayList<>(List.of(subId)));

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        subscriptionService.deleteSubscription(subId, userId);

        assertFalse(user.getSubscriptionList().contains(subId));
        verify(subscriptionRepository).deleteById(subId);
        verify(userRepository).save(user);
    }

    @Test
    void createSubscription_shouldThrowException_whenUserNotFound() {
        String userId = "nonexistent";
        Subscription subscription = new Subscription();
        subscription.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                subscriptionService.createSubscription(subscription));
        assertTrue(ex.getMessage().contains("User not found"));
    }

    @Test
    void createSubscription_shouldThrowException_whenSubscriptionIsNull() {
        assertThrows(NullPointerException.class, () ->
                subscriptionService.createSubscription(null));
    }

    @Test
    void getSubscriptions_shouldReturnEmptyList_whenUserNotFound() {
        String userId = "nonexistent";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        Iterable<Subscription> result = subscriptionService.getSubscriptions(userId);
        assertNotNull(result);
        assertFalse(result.iterator().hasNext());
    }

    @Test
    void getSubscriptions_shouldThrowException_whenUserIdIsNull() {
        assertThrows(NullPointerException.class, () ->
                subscriptionService.getSubscriptions(null));
    }

    @Test
    void getSubscription_shouldThrowException_whenSubscriptionNotFound() {
        String subId = "sub1";
        String userId = "user123";
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.empty());

        assertThrows(SubscriptionException.class, () ->
                subscriptionService.getSubscription(subId, userId));
    }

    @Test
    void getSubscription_shouldThrowException_whenSubscriptionNotOwnedByUser() {
        String subId = "sub1";
        String userId = "user123";
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subId);
        subscription.setUserId("anotherUser");
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));

        assertThrows(SubscriptionException.class, () ->
                subscriptionService.getSubscription(subId, userId));
    }

    @Test
    void getSubscription_shouldThrowException_whenParametersAreNull() {
        assertThrows(NullPointerException.class, () ->
                subscriptionService.getSubscription(null, "user123"));
        assertThrows(NullPointerException.class, () ->
                subscriptionService.getSubscription("sub1", null));
    }

    @Test
    void deleteSubscription_shouldThrowException_whenSubscriptionNotFound() {
        String subId = "sub1";
        String userId = "user123";
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.empty());

        assertThrows(SecurityException.class, () ->
                subscriptionService.deleteSubscription(subId, userId));
    }

    @Test
    void deleteSubscription_shouldThrowException_whenSubscriptionNotOwnedByUser() {
        String subId = "sub1";
        String userId = "user123";
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subId);
        subscription.setUserId("anotherUser");
        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));

        assertThrows(SecurityException.class, () ->
                subscriptionService.deleteSubscription(subId, userId));
    }

    @Test
    void deleteSubscription_shouldHandleCaseWhenUserNotFoundDuringCleanup() {
        String subId = "sub1";
        String userId = "user123";
        Subscription subscription = new Subscription();
        subscription.setSubscriptionId(subId);
        subscription.setUserId(userId);

        when(subscriptionRepository.findById(subId)).thenReturn(Optional.of(subscription));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> subscriptionService.deleteSubscription(subId, userId));
        verify(subscriptionRepository).deleteById(subId);
    }

    @Test
    void deleteSubscription_shouldThrowException_whenParametersAreNull() {
        assertThrows(NullPointerException.class, () ->
                subscriptionService.deleteSubscription(null, "user123"));
        assertThrows(NullPointerException.class, () ->
                subscriptionService.deleteSubscription("sub1", null));
    }
}
