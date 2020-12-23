package com.github.emcat.metric_calculators;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

public class MethodMetricsAggregator {
    private Parser javaParser = new JavaLanguageModule()
            .getDefaultVersion()
            .getLanguageVersionHandler()
            .getParser(new ParserOptions());

    public MethodMetrics calculateSingleMethodMetrics(final MethodDescriptor methodDescriptor) throws IOException, ParseException, NoSuchElementException {
        ASTMethodDeclaration methodDeclaration = getMethodDeclaration(
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

    private ASTMethodDeclaration getMethodDeclaration(String filename, String className, String methodName) throws IOException, ParseException, NoSuchElementException {
        ASTClassOrInterfaceDeclaration classDeclaration = getClassDeclaration(filename, className);
        for (ASTAnyTypeBodyDeclaration classInnerDeclaration : classDeclaration.getDeclarations()) {
            if (classInnerDeclaration.getKind() == ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD) {
                ASTMethodDeclaration methodDeclaration = (ASTMethodDeclaration) classInnerDeclaration.getDeclarationNode();
                if (methodDeclaration.getName().equals(methodName)) {
                    return methodDeclaration;
                }
            }
        }

        throw new NoSuchElementException(String.format("There is no method named %s in class named %s in file %s", methodName, className, filename));
    }

    private ASTClassOrInterfaceDeclaration getClassDeclaration(String filename, String className) throws IOException, ParseException, NoSuchElementException {
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

        throw new NoSuchElementException(String.format("There is no class named %s in file %s", className, filename));
    }

    private ASTCompilationUnit getCompilationUnit(String filename) throws IOException, ParseException {
        try (Reader sourceCodeReader = new FileReader(filename)) {
            return (ASTCompilationUnit) javaParser.parse(filename, sourceCodeReader);
        }
    }
}
