package com.github.emcat.metric_calculators;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class MethodMetricsAggregatorTest {

    @Test
    public void testMethodMetrics() {
        MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        MethodDescriptor methodDescriptor = new MethodDescriptor(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
                "methodExample"
        );

        MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertFalse(methodMetricsAggregator.isErrorOccurred(), String.valueOf(methodMetricsAggregator.getException()));
        assertNotNull(methodMetrics);
        assertEquals(4, methodMetrics.Ncss());
        assertEquals(2, methodMetrics.CyclomaticComplexity());
    }

    @Test
    public void testNotExistingFile() {
        MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        MethodDescriptor methodDescriptor = new MethodDescriptor(
                "NOT_EXISTING_FILE_PATH",
                "JavaSourceCodeExample",
                "methodExample"
        );

        MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertTrue(methodMetricsAggregator.isErrorOccurred());
        assertNull(methodMetrics);
        assertEquals(methodMetricsAggregator.getException().getMethodDescriptor(), methodDescriptor);
        assertEquals(methodMetricsAggregator.getException().getException().getClass(), FileNotFoundException.class);
    }

    @Test
    public void testNotExistingClass() {
        MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        MethodDescriptor methodDescriptor = new MethodDescriptor(
                "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
                "NOT_EXISTING_CLASS",
                "methodExample"
        );

        MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertTrue(methodMetricsAggregator.isErrorOccurred());
        assertNull(methodMetrics);
        assertEquals(methodMetricsAggregator.getException().getMethodDescriptor(), methodDescriptor);
        assertEquals(methodMetricsAggregator.getException().getException().getClass(), RuntimeException.class);
    }

    @Test
    public void testNotExistingMethod() {
        MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        MethodDescriptor methodDescriptor = new MethodDescriptor(
                "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
                "JavaSourceCodeExample",
                "NOT_EXISTING_METHOD"
        );

        MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertTrue(methodMetricsAggregator.isErrorOccurred());
        assertNull(methodMetrics);
        assertEquals(methodMetricsAggregator.getException().getMethodDescriptor(), methodDescriptor);
        assertEquals(methodMetricsAggregator.getException().getException().getClass(), RuntimeException.class);
    }
}
