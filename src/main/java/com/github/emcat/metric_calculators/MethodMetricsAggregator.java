package com.github.emcat.metric_calculators;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.*;

import com.google.common.collect.Streams;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.stream.Stream;

public class MethodMetricsAggregator {
    private final transient Parser javaParser = new JavaLanguageModule()
            .getDefaultVersion()
            .getLanguageVersionHandler()
            .getParser(new ParserOptions());

    public MethodMetrics calculateSingleMethodMetrics(final MethodDescriptor methodDescriptor) throws IOException {
        final ASTMethodDeclaration methodDeclaration = getMethodDeclaration(
                methodDescriptor.getFilePath(),
                methodDescriptor.getClassName(),
                methodDescriptor.getMethodName()
        );

        return new MethodMetrics(
            methodDescriptor.getFilePath(),
            methodDescriptor.getClassName(),
            methodDescriptor.getMethodName(),
            MethodMetricsCalculationUtils.calculateNcss(methodDeclaration),
            MethodMetricsCalculationUtils.calculateCyclomaticComplexityfinal(methodDeclaration)
        );
    }

    private ASTMethodDeclaration getMethodDeclaration(
            final String filename,
            final String className,
            final String methodName
    ) throws IOException {
        final ASTClassOrInterfaceDeclaration classDeclaration = getClassDeclaration(filename, className);
        return Streams.stream(classDeclaration.getDeclarations().iterator())
            .filter(declaration -> declaration.getKind() == ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD)
            .map(declaration -> (ASTMethodDeclaration) declaration.getDeclarationNode())
            .filter(declaration -> declaration.getName().equals(methodName))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(
                String.format("There is no method named %s in class named %s in file %s", methodName, className, filename)
            ));
    }

    private ASTClassOrInterfaceDeclaration getClassDeclaration(
            final String filename,
            final String className
    ) throws IOException {
        final ASTCompilationUnit compilationUnit = getCompilationUnit(filename);
        return Streams.stream(compilationUnit.children())
            .filter(item -> item instanceof  ASTTypeDeclaration)
            .map(item -> Streams.stream(item.children()))
            .reduce(Stream.empty(), Streams::concat)
            .filter(item -> item instanceof ASTClassOrInterfaceDeclaration)
            .map(item -> (ASTClassOrInterfaceDeclaration) item)
            .filter(classDeclaration ->
                !classDeclaration.isInterface() && classDeclaration.getSimpleName().equals(className))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(
                    String.format("There is no class named %s in file %s", className, filename)
            ));
    }

    private ASTCompilationUnit getCompilationUnit(final String filename) throws IOException {
        try (Reader sourceCodeReader = new FileReader(filename)) {
            return (ASTCompilationUnit) javaParser.parse(filename, sourceCodeReader);
        }
    }
}
