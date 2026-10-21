package com.subtracker.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExceptionTest {

    @Test
    void testUserExceptionWithMessage() {
        UserException exception = new UserException("User error occurred");
        assertEquals("User error occurred", exception.getMessage());
    }

    @Test
    void testUserExceptionWithThrowable() {
        Throwable cause = new RuntimeException("Underlying cause");
        UserException exception = new UserException(cause);
        assertEquals(cause, exception.getCause());
    }

    @Test
    void testSubscriptionExceptionWithMessage() {
        SubscriptionException exception = new SubscriptionException("Subscription error occurred");
        assertEquals("Subscription error occurred", exception.getMessage());
    }

    @Test
    void testSubscriptionExceptionWithThrowable() {
        Throwable cause = new RuntimeException("Underlying cause");
        SubscriptionException exception = new SubscriptionException(cause);
        assertEquals(cause, exception.getCause());
    }
}
