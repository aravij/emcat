package com.github.emcat.metric_calculators;

import com.github.emcat.source_code_method.SourceCodeMethodFactory;
import com.github.emcat.source_code_method.SourceCodeMethod;
import com.github.emcat.source_code_method.SourceCodeMethodDescriptor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("PMD.LawOfDemeter")
public class SourceCodeMethodFactoryTest {
    private final static String SOURCE_CODE_EXAMPLE_PATH =
        "./src/test/java/com/github/emcat/metric_calculators/JavaSourceCodeExample.java";

    private final static String CLASS_NAME = "JavaSourceCodeExample";

    private final static String METHOD_NAME = "methodExample";

    @Test
    public void testMethodMetrics() throws IOException {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            SOURCE_CODE_EXAMPLE_PATH,
            CLASS_NAME,
            METHOD_NAME
        );
        final SourceCodeMethod sourceCodeMethod = SourceCodeMethodFactory.createSourCodeMethod(sourceCodeMethodDescriptor);

        assertEquals(5, sourceCodeMethod.getNcss(), "Failed to calculate NCSS right");
        assertEquals(2, sourceCodeMethod.getCyclomaticComplexity(), "Failed to calculate Cyclomatic complexity right");
    }

    @Test
    public void testNotExistingFile() {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            "NOT_EXISTING_FILE_PATH",
            CLASS_NAME,
            METHOD_NAME
        );

        assertThrows(
            IOException.class,
            () -> {
                SourceCodeMethodFactory.createSourCodeMethod(sourceCodeMethodDescriptor);
            },
            "Expects RunTimeException when looking for not existing file"
        );
    }

    @Test
    public void testNotExistingClass() {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            SOURCE_CODE_EXAMPLE_PATH,
            "NOT_EXISTING_CLASS",
            METHOD_NAME
        );

        assertThrows(
            NoSuchElementException.class,
            () -> {
                SourceCodeMethodFactory.createSourCodeMethod(sourceCodeMethodDescriptor);
            },
            "Expects RunTimeException when looking for not existing class"
        );
    }

    @Test
    public void testNotExistingMethod() {
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor = new SourceCodeMethodDescriptor(
            SOURCE_CODE_EXAMPLE_PATH,
            CLASS_NAME,
            "NOT_EXISTING_METHOD"
        );

        assertThrows(
            NoSuchElementException.class,
            () -> {
                SourceCodeMethodFactory.createSourCodeMethod(sourceCodeMethodDescriptor);
            },
            "Expects RunTimeException when looking for not existing method"
        );
    }

    @Test
    public void testDiscovery() throws IOException {
        final List<SourceCodeMethod> methods = SourceCodeMethodFactory.createAllSourceCodeMethodFromFile(SOURCE_CODE_EXAMPLE_PATH);

        final List<SourceCodeMethodDescriptor> methodDescriptors = methods
            .stream()
            .map(SourceCodeMethodDescriptor::new)
            .collect(Collectors.toList());

        final SourceCodeMethodDescriptor[] expectedMethodDescriptors = {
                new SourceCodeMethodDescriptor(SOURCE_CODE_EXAMPLE_PATH, CLASS_NAME, METHOD_NAME),
                new SourceCodeMethodDescriptor(SOURCE_CODE_EXAMPLE_PATH, CLASS_NAME, "anotherMethod"),
                new SourceCodeMethodDescriptor(SOURCE_CODE_EXAMPLE_PATH, "AnotherClass", "someMethod")
        };

        assertArrayEquals(methodDescriptors.toArray(), expectedMethodDescriptors, "Failed to discover all methods");

    }
}
