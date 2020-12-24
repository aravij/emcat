package com.github.emcat;

import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import net.sourceforge.pmd.lang.ast.ParseException;
import org.apache.commons.collections.comparators.FixedOrderComparator;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.*;
import java.util.NoSuchElementException;

@Command(
    name = "emcat",
    mixinStandardHelpOptions = true,
    description = "Calculates NCSS and Cyclomatic complexity of Java methods.",
    version = "1.0"
)
public class Emcat {
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
            final SourceCodeMethodDescriptor methodDescriptor = new SourceCodeMethodDescriptor(
                sourceCodeFilePath,
                className,
                methodName
            );

            final SourceCodeMethod method = AstMethodFinder.findAstMethod(methodDescriptor);

            System.out.println("NCSS: " + method.getNcss());
            System.out.println("Cyclomatic complexity: " + method.getCyclomaticComplexity());
            return 0;
        }
        catch (IOException | NoSuchElementException | ParseException e) {
            System.err.println("ERROR: " + e);
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
            final CsvToBean<SourceCodeMethodDescriptor> batchInput = createBatchInput(batchRawInput);

            @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
            final StatefulBeanToCsv<SourceCodeMethod> batchOutput = createBatchOutput(batchRawOutput);

            for (final SourceCodeMethodDescriptor methodDescriptor : batchInput.parse()) {
                try {
                    final SourceCodeMethod method = AstMethodFinder.findAstMethod(methodDescriptor);
                    batchOutput.write(method);
                }
                catch (NoSuchElementException | IOException | ParseException e) {
                    System.err.println("WARNING: " + e);
                }
            }

            return 0;
        }
    }

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    public static void main(final String... args) {
        System.exit(new CommandLine(new Emcat()).execute(args));
    }

    private static CsvToBean<SourceCodeMethodDescriptor> createBatchInput(final Reader batchRawInput) {
        final HeaderColumnNameMappingStrategy<SourceCodeMethodDescriptor> columnNameOrder =
            new HeaderColumnNameMappingStrategy<>();

        columnNameOrder.setType(SourceCodeMethodDescriptor.class);
        columnNameOrder.setColumnOrderOnWrite(
            new FixedOrderComparator(new String[]{
                "FILE_PATH",
                "CLASS_NAME",
                "METHOD_NAME"
            })
        );

        return new CsvToBeanBuilder<SourceCodeMethodDescriptor>(batchRawInput)
            .withType(SourceCodeMethodDescriptor.class)
            .withMappingStrategy(columnNameOrder)
            .build();
    }

    private static StatefulBeanToCsv<SourceCodeMethod> createBatchOutput(final Writer batchRawOutput) {
        final HeaderColumnNameMappingStrategy<SourceCodeMethod> columnNameOrder = new HeaderColumnNameMappingStrategy<>();
        columnNameOrder.setType(SourceCodeMethod.class);
        columnNameOrder.setColumnOrderOnWrite(
            new FixedOrderComparator(new String[]{
                "FILE_PATH",
                "CLASS_NAME",
                "METHOD_NAME",
                "NCSS",
                "CYCLOMATIC_COMPLEXITY"
            })
        );

        return new StatefulBeanToCsvBuilder<SourceCodeMethod>(batchRawOutput)
            .withMappingStrategy(columnNameOrder)
            .build();
    }
}
