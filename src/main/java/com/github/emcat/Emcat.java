package com.github.emcat;

import picocli.CommandLine;
import picocli.CommandLine.Option;
import picocli.CommandLine.Command;

import java.util.concurrent.Callable;

import com.github.emcat.metric_calculators.*;

@Command(name = "emcat", description = "Calculates given NCSS and cyclomatic complexity of a given method.", version = "1.0")
public class Emcat implements Callable<Integer> {
    public static void main(String... args) {
        System.exit(new CommandLine(new Emcat()).execute(args));
    }

    @Override
    public Integer call() {
        MethodMetricsAggregator methodMetricsAggregator = new MethodMetricsAggregator();
        MethodDescriptor methodDescriptor = new MethodDescriptor(sourceCodeFilePath, className, methodName);
        MethodMetricsData methodMetrics = methodMetricsAggregator.calculateSingleMethodMetrics(methodDescriptor);

        if (methodMetricsAggregator.isErrorOccurred()) {
            System.err.println("Failed to calculate metrics");
            System.err.println(methodMetricsAggregator.getException().toString());
            return 1;
        }

        assert methodMetrics != null;

        System.out.println("NCSS: " + methodMetrics.Ncss());
        System.out.println("Cyclomatic complexity: " + methodMetrics.CyclomaticComplexity());
        return 0;
    }

    @Option(names = {"-f", "--file"}, required = true, description = "File path to Java source code to analyse")
    private String sourceCodeFilePath;

    @Option(names = {"-c", "--class_name"}, required = true, description = "Name of the class to analyse")
    private String className;

    @Option(names = {"-m", "--method_name"}, required = true, description = "Method name to analyse")
    private String methodName;
}
