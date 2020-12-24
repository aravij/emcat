package com.github.emcat;

import com.github.emcat.source_code_method.SourceCodeMethod;
import com.github.emcat.source_code_method.SourceCodeMethodDescriptor;
import com.github.emcat.source_code_method.SourceCodeMethodFactory;
import com.opencsv.bean.*;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import net.sourceforge.pmd.lang.ast.ParseException;
import org.apache.commons.collections.comparators.FixedOrderComparator;
import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static java.lang.Integer.MAX_VALUE;

@Command(
    name = "emcat",
    mixinStandardHelpOptions = true,
    description = "Calculates NCSS and Cyclomatic complexity of Java methods.",
    version = "1.0"
)
public class Emcat {
    @Command(mixinStandardHelpOptions = true, description = "Calculates metric of single method and print to output")
    public int single(
        @Option(names = {"-f", "--file"}, required = true, description = "File path to Java source code to analyze")
        final String sourceCodeFilePath,
        @Option(names = {"-c", "--class_name"}, required = true, description = "Name of the class to analyze")
        final String className,
        @Option(names = {"-m", "--method_name"}, required = true, description = "Method name to analyze")
        final String methodName
    ) throws IOException {
        final SourceCodeMethodDescriptor methodDescriptor = new SourceCodeMethodDescriptor(
            sourceCodeFilePath,
            className,
            methodName
        );

        final SourceCodeMethod method = SourceCodeMethodFactory.createSourCodeMethod(methodDescriptor);

        System.out.println("NCSS: " + method.getNcss());
        System.out.println("Cyclomatic complexity: " + method.getCyclomaticComplexity());
        return 0;

    }

    @Command(mixinStandardHelpOptions = true, description = "Calculate metrics of all method described in batch file")
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
                    final SourceCodeMethod method = SourceCodeMethodFactory.createSourCodeMethod(methodDescriptor);
                    batchOutput.write(method);
                }
                catch (NoSuchElementException | IOException | ParseException e) {
                    System.err.println("WARNING: " + e);
                }
            }

            return 0;
        }
    }

    @Command(mixinStandardHelpOptions = true, description = "Finds all methods in all files in file tree under provided directory ")
    public int discover(
        @Option(
            names = {"-d", "--directory"},
            required = true,
            description = "Root directory of discovery, keeps files with '.java' extension only"
        )
        final String rootPath,
        @Option(names = {"-o", "--output"}, required = true, description = "Path to output file")
        final String outputPath
    ) throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        try (Writer batchRawOutput = new FileWriter(new File(outputPath))) {
            @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
            final StatefulBeanToCsv<SourceCodeMethod> output = createBatchOutput(batchRawOutput);

            final List<Path> paths = Files.find(
                    Path.of(rootPath),
                    MAX_VALUE,
                    (path, attrs) -> path.toString().endsWith(".java"),
                    FileVisitOption.FOLLOW_LINKS
                )
                .collect(Collectors.toList());

            for (final Path path : paths) {
                try {
                    output.write(SourceCodeMethodFactory.createAllSourceCodeMethodFromFile(String.valueOf(path)));
                }
                catch (IOException | ParseException e) {
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
