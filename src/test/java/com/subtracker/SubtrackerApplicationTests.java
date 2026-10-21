package com.subtracker;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SubtrackerApplicationTests {

    @Test
    void testApplicationInitialization() {
        SubtrackerApplication application = new SubtrackerApplication();
        assertNotNull(application);
    }

}
