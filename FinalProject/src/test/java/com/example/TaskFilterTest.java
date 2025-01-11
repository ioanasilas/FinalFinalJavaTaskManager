package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TaskFilterTest {

    @Test
    public void testConstructor() {
        // create a TaskFilter object with a sample filename
        String testFilename = "sample_tasks.csv";
        TaskFilter taskFilter = new TaskFilter(testFilename);

        // verify that the filename is correctly assigned
        assertEquals(testFilename, taskFilter.filename, "the filename should match the one provided to the constructor");
    }
}
