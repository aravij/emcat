package com.github.emcat.metric_calculators;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.LawOfDemeter")
public class MethodMetricsAggregatorTest {
    @Test
    public void testMethodMetrics() throws IOException {
        final MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        final MethodDescriptor methodDescriptor = new MethodDescriptor(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
                "methodExample"
        );

        final MethodMetrics methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);
        assertEquals(5, methodMetrics.getNcss(), "Failed to calculate NCSS right");
        assertEquals(2, methodMetrics.getCyclomaticComplexity(), "Failed to calculate Cyclomatic complexity right");
    }

    @Test
    public void testNotExistingFile() {
        assertThrows(IOException.class,
                () -> new MethodMetricsAggregator().calculateSingleMethodMetrics(new MethodDescriptor(
                        "NOT_EXISTING_FILE_PATH",
                        "JavaSourceCodeExample",
                        "methodExample"
                )),
                "Expects RunTimeException when looking for not existing file"
        );
    }

    @Test
    public void testNotExistingClass() {
        assertThrows(NoSuchElementException.class,
                () -> new MethodMetricsAggregator().calculateSingleMethodMetrics(new MethodDescriptor(
                        "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
                        "NOT_EXISTING_CLASS",
                        "methodExample"
                )),
                "Expects RunTimeException when looking for not existing class"
        );
    }

    @Test
    public void testNotExistingMethod() {
        assertThrows(NoSuchElementException.class,
                () -> new MethodMetricsAggregator().calculateSingleMethodMetrics(new MethodDescriptor(
                        "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
                        "JavaSourceCodeExample",
                        "NOT_EXISTING_METHOD"
                )),
                "Expects RunTimeException when looking for not existing method"
        );
    }
}
