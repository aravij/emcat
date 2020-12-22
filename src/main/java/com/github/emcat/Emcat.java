package com.github.emcat;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import com.github.emcat.metric_calculators.MethodDescriptor;
import com.github.emcat.metric_calculators.MethodMetricsAggregator;
import com.github.emcat.metric_calculators.MethodMetricsData;

@Command(name = "emcat", mixinStandardHelpOptions = true, description = "Calculates NCSS and Cyclomatic complexity of Java methods.", version = "1.0")
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
        final MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        final MethodDescriptor methodDescriptor = new MethodDescriptor(sourceCodeFilePath, className, methodName);
        final MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);

        int exitCode;
        if (methodMetrics == null) {
            System.err.println("Failed to calculate metrics");
            System.err.println(methodMetricsAggregator.getException());
            exitCode = 1;
        }
        else
        {
            System.out.println("NCSS: " + methodMetrics.Ncss());
            System.out.println("Cyclomatic complexity: " + methodMetrics.CyclomaticComplexity());
            exitCode = 0;
        }

        return exitCode;
    }

    @SuppressWarnings("PMD.DoNotCallSystemExit")
    public static void main(final String... args) {
        System.exit(new CommandLine(new Emcat()).execute(args));
    }
}
