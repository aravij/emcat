package com.github.emcat;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import net.sourceforge.pmd.lang.ast.ParseException;
import org.apache.commons.collections.comparators.FixedOrderComparator;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import com.github.emcat.metric_calculators.MethodDescriptor;
import com.github.emcat.metric_calculators.MethodMetricsAggregator;
import com.github.emcat.metric_calculators.MethodMetrics;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Command(
    name = "emcat",
    mixinStandardHelpOptions = true,
    description = "Calculates NCSS and Cyclomatic complexity of Java methods.",
    version = "1.0"
)
public class Emcat {
    private final transient MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();

    @Command(mixinStandardHelpOptions = true)
    public int single(
        @Option(names = {"-f", "--file"}, required = true, description = "File path to Java source code to analyse")
        final String sourceCodeFilePath,
        @Option(names = {"-c", "--class_name"}, required = true, description = "Name of the class to analyse")
        final String className,
        @Option(names = {"-m", "--method_name"}, required = true, description = "Method name to analyse")
        final String methodName
    ) {
        try {
            final MethodDescriptor methodDescriptor = new MethodDescriptor(sourceCodeFilePath, className, methodName);
            final MethodMetrics methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);

            System.out.println("NCSS: " + methodMetrics.getNcss());
            System.out.println("Cyclomatic complexity: " + methodMetrics.getCyclomaticComplexity());
            return 0;
        }
        catch (IOException | NoSuchElementException | ParseException e) {
            System.err.printf("ERROR: " + e);
        }

        return 1;
    }

    @Command(mixinStandardHelpOptions = true)
    public int batch(
        @Option(names = {"-f", "--file"}, required = true, description = "File path to CSV file")
        final String batchFilePath,
        @Option(names = {"-o", "--output"}, required = true, description = "Path to output file")
        final String outputPath
    ) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        if (batchFilePath.equals(outputPath)) {
            System.err.println("ERROR: Output file can't be the same as batch input file.");
            return 2;
        }

        try (
            Reader batchRawInput = new FileReader(batchFilePath);
            Writer batchRawOutput = new FileWriter(new File(outputPath))
        ) {
            final CsvToBean<MethodDescriptor> batchInput = createCsvReader(batchRawInput);

            final List<MethodMetrics> methodMetrics = new ArrayList<>();
            for (final MethodDescriptor methodDescriptor : batchInput.parse()) {
                try {
                    methodMetrics.add(methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor));
                }
                catch (NoSuchElementException | IOException | ParseException e) {
                    System.err.println("WARNING: " + e);
                }
            }

            if (methodMetrics.isEmpty()) {
                System.err.println("ERROR: Nothing to write to output.");
                return 1;
            } else {
                final StatefulBeanToCsv batchOutput = createCsvWriter(batchRawOutput);
                batchOutput.write(methodMetrics);
            }

            return 0;
        }
    }

    private CsvToBean<MethodDescriptor> createCsvReader(final Reader batchRawInput) {
        final HeaderColumnNameMappingStrategy columnOrderInputStrategy = new HeaderColumnNameMappingStrategy<>();
        columnOrderInputStrategy.setType(MethodDescriptor.class);
        columnOrderInputStrategy.setColumnOrderOnWrite(
                new FixedOrderComparator(new String[]{
                        "FILE_PATH",
                        "CLASS_NAME",
                        "METHOD_NAME",
                })
        );

        return new CsvToBeanBuilder(batchRawInput)
            .withType(MethodDescriptor.class)
            .withMappingStrategy(columnOrderInputStrategy)
            .build();
    }

    private StatefulBeanToCsv createCsvWriter(final Writer batchRawOutput) {
        final HeaderColumnNameMappingStrategy columnOrderOutputStrategy = new HeaderColumnNameMappingStrategy<>();
        columnOrderOutputStrategy.setType(MethodMetrics.class);
        columnOrderOutputStrategy.setColumnOrderOnWrite(
                new FixedOrderComparator(new String[]{
                        "FILE_PATH",
                        "CLASS_NAME",
                        "METHOD_NAME",
                        "NCSS",
                        "CYCLOMATIC_COMPLEXITY"
                })
        );

        return new StatefulBeanToCsvBuilder(batchRawOutput)
                .withMappingStrategy(columnOrderOutputStrategy)
                .build();
    }

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    public static void main(final String... args) {
        System.exit(new CommandLine(new Emcat()).execute(args));
    }
}
