package com.github.emcat.metric_calculators;

import com.github.emcat.AstMethodFinder;
import com.github.emcat.SourceCodeMethod;
import com.github.emcat.SourceCodeMethodDescriptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.LawOfDemeter")
public class AstMethodFinderTest {
    @Test
    public void testMethodMetrics() throws IOException {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
            "methodExample"
        );
        final SourceCodeMethod sourceCodeMethod = AstMethodFinder.findAstMethod(sourceCodeMethodDescriptor);

        assertEquals(5, sourceCodeMethod.getNcss(), "Failed to calculate NCSS right");
        assertEquals(2, sourceCodeMethod.getCyclomaticComplexity(), "Failed to calculate Cyclomatic complexity right");
    }

    @Test
    public void testNotExistingFile() {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            "NOT_EXISTING_FILE_PATH",
            "JavaSourceCodeExample",
            "methodExample"
        );

        assertThrows(
            IOException.class,
            () -> {
                AstMethodFinder.findAstMethod(sourceCodeMethodDescriptor);
            },
            "Expects RunTimeException when looking for not existing file"
        );
    }

    @Test
    public void testNotExistingClass() {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "NOT_EXISTING_CLASS",
            "methodExample"
        );

        assertThrows(
            NoSuchElementException.class,
            () -> {
                AstMethodFinder.findAstMethod(sourceCodeMethodDescriptor);
            },
            "Expects RunTimeException when looking for not existing class"
        );
    }

    @Test
    public void testNotExistingMethod() {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java",
            "JavaSourceCodeExample",
            "NOT_EXISTING_METHOD"
        );

        assertThrows(
            NoSuchElementException.class,
            () -> {
                AstMethodFinder.findAstMethod(sourceCodeMethodDescriptor);
            },
            "Expects RunTimeException when looking for not existing method"
        );
    }
}
