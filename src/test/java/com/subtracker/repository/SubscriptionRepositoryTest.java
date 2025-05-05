package com.subtracker.repository;

import com.subtracker.model.Subscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SubscriptionRepositoryTest {

    @Mock
    private ReactiveSubscriptionRepository reactiveSubscriptionRepository;

    @InjectMocks
    private SubscriptionRepository subscriptionRepository;

    private final Subscription dummySubscription = new Subscription();
    private final String subscriptionId = "abc123";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        subscriptionRepository = new SubscriptionRepository(reactiveSubscriptionRepository);
    }

    @Test
    void save_shouldReturnSavedSubscription() {
        when(reactiveSubscriptionRepository.save(dummySubscription))
                .thenReturn(Mono.just(dummySubscription));

        Subscription result = subscriptionRepository.save(dummySubscription);
        assertEquals(dummySubscription, result);
    }

    @Test
    void findById_shouldReturnOptionalWithSubscription() {
        when(reactiveSubscriptionRepository.findById(subscriptionId))
                .thenReturn(Mono.just(dummySubscription));

        Optional<Subscription> result = subscriptionRepository.findById(subscriptionId);
        assertTrue(result.isPresent());
        assertEquals(dummySubscription, result.get());
    }

    @Test
    void findById_shouldReturnEmptyOptionalWhenNotFound() {
        when(reactiveSubscriptionRepository.findById(subscriptionId))
                .thenReturn(Mono.empty());

        Optional<Subscription> result = subscriptionRepository.findById(subscriptionId);
        assertFalse(result.isPresent());
    }

    @Test
    void deleteById_shouldInvokeReactiveDelete() {
        when(reactiveSubscriptionRepository.deleteById(subscriptionId))
                .thenReturn(Mono.empty());

        assertDoesNotThrow(() -> subscriptionRepository.deleteById(subscriptionId));
        verify(reactiveSubscriptionRepository).deleteById(subscriptionId);
    }

    @Test
    void findAll_shouldReturnListOfSubscriptions() {
        List<Subscription> subscriptions = Arrays.asList(new Subscription(), new Subscription());

        when(reactiveSubscriptionRepository.findAll())
                .thenReturn(Flux.fromIterable(subscriptions));

        List<Subscription> result = subscriptionRepository.findAll();
        assertEquals(subscriptions.size(), result.size());
    }

    @Test
    void findAllById_shouldReturnMatchingSubscriptions() {
        List<String> ids = Arrays.asList("id1", "id2");
        List<Subscription> subscriptions = Arrays.asList(new Subscription(), new Subscription());

        when(reactiveSubscriptionRepository.findAllById(ids))
                .thenReturn(Flux.fromIterable(subscriptions));

        List<Subscription> result = subscriptionRepository.findAllById(ids);
        assertEquals(subscriptions.size(), result.size());
    }

    @Test
    void save_shouldThrowIfReactiveThrows() {
        when(reactiveSubscriptionRepository.save(dummySubscription))
                .thenReturn(Mono.error(new RuntimeException("DB error")));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> subscriptionRepository.save(dummySubscription));
        assertEquals("DB error", ex.getMessage());
    }

    @Test
    void findById_shouldThrowIfReactiveThrows() {
        when(reactiveSubscriptionRepository.findById(subscriptionId))
                .thenReturn(Mono.error(new RuntimeException("not found")));

        assertThrows(RuntimeException.class, () -> subscriptionRepository.findById(subscriptionId));
    }
}
