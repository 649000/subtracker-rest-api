package com.subtracker.repository;

import com.subtracker.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import reactor.core.publisher.Mono;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserRepositoryTest {

    private ReactiveUserRepository reactiveRepo;
    private UserRepository userRepo;

    private final User sampleUser = new User();

    @BeforeEach
    void setUp() {
        reactiveRepo = Mockito.mock(ReactiveUserRepository.class);
        userRepo = new UserRepository(reactiveRepo);
        sampleUser.setUid("123");
        sampleUser.setEmail("email@subtracker.com");
    }

    @Test
    void findById_shouldReturnUser_whenExists() {
        when(reactiveRepo.findById("123")).thenReturn(Mono.just(sampleUser));

        Optional<User> result = userRepo.findById("123");

        assertTrue(result.isPresent());
        assertEquals("123", result.get().getUid());
        verify(reactiveRepo).findById("123");
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        when(reactiveRepo.findById("123")).thenReturn(Mono.empty());

        Optional<User> result = userRepo.findById("123");

        assertFalse(result.isPresent());
        verify(reactiveRepo).findById("123");
    }

    @Test
    void findById_shouldThrow_whenErrorOccurs() {
        when(reactiveRepo.findById("123")).thenReturn(Mono.error(new RuntimeException("DB failure")));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userRepo.findById("123"));
        assertEquals("DB failure", thrown.getMessage());
        verify(reactiveRepo).findById("123");
    }

    @Test
    void save_shouldReturnSavedUser() {
        when(reactiveRepo.save(sampleUser)).thenReturn(Mono.just(sampleUser));

        User saved = userRepo.save(sampleUser);

        assertNotNull(saved);
        assertEquals("123", saved.getUid());
        verify(reactiveRepo).save(sampleUser);
    }

    @Test
    void save_shouldThrow_whenErrorOccurs() {
        when(reactiveRepo.save(sampleUser)).thenReturn(Mono.error(new RuntimeException("Save failed")));

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> userRepo.save(sampleUser));
        assertEquals("Save failed", thrown.getMessage());
        verify(reactiveRepo).save(sampleUser);
    }

    @Test
    void save_shouldThrow_whenMonoReturnsNull() {
        when(reactiveRepo.save(sampleUser)).thenReturn(Mono.justOrEmpty(null)); // simulate null

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> userRepo.save(sampleUser));
        assertEquals("Saved user is null", thrown.getMessage());
        verify(reactiveRepo).save(sampleUser);
    }
}
