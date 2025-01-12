package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskFilterTest {

    @Test
    public void testConstructor() {
        // create a TaskFilter instance
        TaskFilter filter = new TaskFilter();

        // assert that the instance is not null
        assertNotNull(filter, "TaskFilter instance should be created successfully.");
    }
}
