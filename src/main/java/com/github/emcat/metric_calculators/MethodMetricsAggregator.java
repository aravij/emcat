package com.github.emcat.metric_calculators;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class MethodMetricsAggregator {
    public MethodMetricsData calculateSingleMethodMetrics(MethodDescriptor methodDescriptor) {
        try {
            exception = null;

            ASTMethodDeclaration methodDeclaration = getMethodDeclaration(
                    methodDescriptor.filepath(),
                    methodDescriptor.className(),
                    methodDescriptor.methodName()
            );

            return metricsCalculator.calculateMethodMetrics(methodDeclaration);
        }
        catch (Exception exception) {
            this.exception = new MethodProcessingException(methodDescriptor, exception);
            return null;
        }
    }

    public MethodProcessingException getException() {
        return exception;
    }

    private ASTMethodDeclaration getMethodDeclaration(String filename, String className, String methodName) throws IOException {
        ASTClassOrInterfaceDeclaration classDeclaration = getClassDeclaration(filename, className);
        for (ASTAnyTypeBodyDeclaration classInnerDeclaration : classDeclaration.getDeclarations()) {
            if (classInnerDeclaration.getKind() == ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD) {
                ASTMethodDeclaration methodDeclaration = (ASTMethodDeclaration) classInnerDeclaration.getDeclarationNode();
                if (methodDeclaration.getName().equals(methodName)) {
                    return methodDeclaration;
                }
            }
        }

        throw new RuntimeException(String.format("There is no method named %s in class named %s in file %s", methodName, className, filename));
    }

    private ASTClassOrInterfaceDeclaration getClassDeclaration(String filename, String className) throws IOException {
        ASTCompilationUnit compilationUnit = getCompilationUnit(filename);
        for (JavaNode child : compilationUnit.children()) {
            if (child instanceof ASTTypeDeclaration) {
                for (JavaNode grandChild : child.children()) {
                    if (grandChild instanceof ASTClassOrInterfaceDeclaration classDeclaration) {
                        if (!classDeclaration.isInterface() && classDeclaration.getSimpleName().equals(className)) {
                            return classDeclaration;
                        }
                    }
                }
            }
        }

        throw new RuntimeException(String.format("There is no class named %s in file %s", className, filename));
    }

    private ASTCompilationUnit getCompilationUnit(String filename) throws IOException {
        try (Reader sourceCodeReader = new FileReader(filename)) {
            return (ASTCompilationUnit) javaParser.parse(filename, sourceCodeReader);
        }
    }

    private Parser javaParser = new JavaLanguageModule().getDefaultVersion().getLanguageVersionHandler().getParser(new ParserOptions());
    private MethodMetricsCalculator metricsCalculator = new MethodMetricsCalculator();
    private MethodProcessingException exception = null;
}
