package com.github.emcat;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import com.github.emcat.metric_calculators.MethodDescriptor;
import com.github.emcat.metric_calculators.MethodMetricsAggregator;
import com.github.emcat.metric_calculators.MethodMetricsData;

@Command(name = "emcat", description = "Calculates given NCSS and cyclomatic complexity of a given method.", version = "1.0")
public class Emcat implements Callable<Integer> {
    @Option(names = {"-f", "--file"}, required = true, description = "File path to Java source code to analyse")
    private transient String sourceCodeFilePath;

    @Option(names = {"-c", "--class_name"}, required = true, description = "Name of the class to analyse")
    private transient String className;

    @Option(names = {"-m", "--method_name"}, required = true, description = "Method name to analyse")
    private transient String methodName;

    @Override
    public Integer call() {
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

    public static void main(final String... args) {
        System.exit(new CommandLine(new Emcat()).execute(args));//NOPMD
    }
}
