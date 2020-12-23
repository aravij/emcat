package com.github.emcat.metric_calculators;

import com.github.emcat.SourceCodeMethod;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.LawOfDemeter")
public class AstMethodFinderTest {
    @Test
    public void testMethodMetrics() throws IOException {
        final SourceCodeMethod sourceCodeMethod = new SourceCodeMethod(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
            "methodExample"
        );
        sourceCodeMethod.calculateMetrics();

        assertEquals(5, sourceCodeMethod.getNcss(), "Failed to calculate NCSS right");
        assertEquals(2, sourceCodeMethod.getCyclomaticComplexity(), "Failed to calculate Cyclomatic complexity right");
    }

    @Test
    public void testNotExistingFile() {
        final SourceCodeMethod sourceCodeMethod = new SourceCodeMethod(
            "NOT_EXISTING_FILE_PATH",
            "JavaSourceCodeExample",
            "methodExample"
        );

        assertThrows(
            IOException.class,
            sourceCodeMethod::calculateMetrics,
            "Expects RunTimeException when looking for not existing file"
        );
    }

    @Test
    public void testNotExistingClass() {
        final SourceCodeMethod sourceCodeMethod = new SourceCodeMethod(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "NOT_EXISTING_CLASS",
            "methodExample"
        );

        assertThrows(
            NoSuchElementException.class,
            sourceCodeMethod::calculateMetrics,
            "Expects RunTimeException when looking for not existing class"
        );
    }

    @Test
    public void testNotExistingMethod() {
        final SourceCodeMethod sourceCodeMethod = new SourceCodeMethod(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
            "NOT_EXISTING_METHOD"
        );

        assertThrows(
            NoSuchElementException.class,
            sourceCodeMethod::calculateMetrics,
            "Expects RunTimeException when looking for not existing method"
        );
    }
}
