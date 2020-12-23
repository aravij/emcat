package com.github.emcat;

import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.java.JavaLanguageModule;
import net.sourceforge.pmd.lang.java.ast.*;

import com.google.common.collect.Streams;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AstMethodFinder {
    private static final Parser JAVA_PARSER = new JavaLanguageModule()
            .getDefaultVersion()
            .getLanguageVersionHandler()
            .getParser(new ParserOptions());

    public ASTMethodDeclaration findAstMethod(
            final String filePath,
            final String className,
            final String methodName
    ) throws IOException {
        final ASTCompilationUnit compilationUnit = getCompilationUnit(filePath);

        final ASTClassOrInterfaceDeclaration classDeclaration = getAllAstCLassDeclarations(compilationUnit)
            .stream()
            .filter(cd -> cd.getSimpleName().equals(className))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(
                String.format("Can't find class %s in file %s.", className, filePath)
            ));

        return getAllAstMethodDeclarations(classDeclaration)
            .stream()
            .filter(md -> md.getName().equals(methodName))
            .findFirst()
            .orElseThrow(() -> new NoSuchElementException(
                String.format("Can't find method %s in class %s in file %s", methodName, className, filePath)
            ));
    }

    public List<ASTMethodDeclaration> getAllAstMethodDeclarations(final ASTClassOrInterfaceDeclaration classDeclaration) {
        return classDeclaration.getDeclarations()
            .stream()
            .filter(declaration -> declaration.getKind() == ASTAnyTypeBodyDeclaration.DeclarationKind.METHOD)
            .map(declaration -> (ASTMethodDeclaration) declaration.getDeclarationNode())
            .collect(Collectors.toList());
    }

    public List<ASTClassOrInterfaceDeclaration> getAllAstCLassDeclarations(final ASTCompilationUnit compilationUnit) {
        return Streams.stream(compilationUnit.children())
            .filter(item -> item instanceof  ASTTypeDeclaration)
            .map(item -> Streams.stream(item.children()))
            .reduce(Stream.empty(), Streams::concat)
            .filter(item -> item instanceof ASTClassOrInterfaceDeclaration)
            .map(item -> (ASTClassOrInterfaceDeclaration) item)
            .filter(declaration -> !declaration.isInterface())
            .collect(Collectors.toList());
    }

    public ASTCompilationUnit getCompilationUnit(final String filename) throws IOException {
        try (Reader sourceCodeReader = new FileReader(filename)) {
            return (ASTCompilationUnit) JAVA_PARSER.parse(filename, sourceCodeReader);
        }
    }
}
