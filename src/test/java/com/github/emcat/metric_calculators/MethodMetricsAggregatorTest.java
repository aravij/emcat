package com.github.emcat.metric_calculators;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.LawOfDemeter")
public class MethodMetricsAggregatorTest {
    @Test
    public void testMethodMetrics() {
        final MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        final MethodDescriptor methodDescriptor = new MethodDescriptor(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
                "methodExample"
        );

        final MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertNotNull(methodMetrics, "MethodMetrics is null\n" + methodMetricsAggregator.getException());
        assertEquals(5, methodMetrics.Ncss(), "Failed to calculate NCSS right");
        assertEquals(2, methodMetrics.CyclomaticComplexity(), "Failed to calculate Cyclomatic complexity right");
    }

    @Test
    public void testNotExistingFile() {
        final MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        final MethodDescriptor methodDescriptor = new MethodDescriptor(
                "NOT_EXISTING_FILE_PATH",
                "JavaSourceCodeExample",
                "methodExample"
        );

        final MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertNull(methodMetrics, "There should be an error in methodMetricsAggregator, but methodMetrics is not null");
        assertEquals(methodMetricsAggregator.getException().getMethodDescriptor(), methodDescriptor, "Error from methodMetricsAggregator does not has the same method descriptor");
        assertEquals(methodMetricsAggregator.getException().getException().getClass(), FileNotFoundException.class, "Exception class must be \"FileNotFoundException\"");
    }

    @Test
    public void testNotExistingClass() {
        final MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        final MethodDescriptor methodDescriptor = new MethodDescriptor(
                "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
                "NOT_EXISTING_CLASS",
                "methodExample"
        );

        final MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertNull(methodMetrics, "There should be an error in methodMetricsAggregator, but methodMetrics is not null");
        assertEquals(methodMetricsAggregator.getException().getMethodDescriptor(), methodDescriptor, "Error from methodMetricsAggregator does not has the same method descriptor");
        assertEquals(methodMetricsAggregator.getException().getException().getClass(), RuntimeException.class, "Exception class must be \"RuntimeException\"");
    }

    @Test
    public void testNotExistingMethod() {
        final MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        final MethodDescriptor methodDescriptor = new MethodDescriptor(
                "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
                "JavaSourceCodeExample",
                "NOT_EXISTING_METHOD"
        );

        final MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertNull(methodMetrics, "There should be an error in methodMetricsAggregator, but methodMetrics is not null");
        assertEquals(methodMetricsAggregator.getException().getMethodDescriptor(), methodDescriptor, "Error from methodMetricsAggregator does not has the same method descriptor");
        assertEquals(methodMetricsAggregator.getException().getException().getClass(), RuntimeException.class, "Exception class must be \"RuntimeException\"");
    }
}
