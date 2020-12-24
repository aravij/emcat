package com.github.emcat;

import com.opencsv.bean.CsvBindByName;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

public class SourceCodeMethod extends SourceCodeMethodDescriptor {
    @CsvBindByName(column = "NCSS")
    private final transient int ncss;

    @CsvBindByName(column = "CYCLOMATIC_COMPLEXITY")
    private final transient int cyclomaticComplexity;

    private final static CycloMetric CYCLOMATIC_COMPLEXITY_CALCULATOR = new CycloMetric();

    private final static NcssMetric.NcssOperationMetric NCSS_CALCULATOR = new NcssMetric.NcssOperationMetric();

    public SourceCodeMethod(
        final SourceCodeMethodDescriptor sourceCodeMethodDescriptor,
        final ASTMethodDeclaration astMethodDeclarationNode
    ) {
        super(sourceCodeMethodDescriptor);

        this.ncss = (int) NCSS_CALCULATOR.computeFor(astMethodDeclarationNode, MetricOptions.emptyOptions());
        this.cyclomaticComplexity = (int) CYCLOMATIC_COMPLEXITY_CALCULATOR.computeFor(
                astMethodDeclarationNode,
                MetricOptions.emptyOptions()
        );
    }

    public SourceCodeMethod(
        final String filePath,
        final String className,
        final String methodName,
        final ASTMethodDeclaration astMethodDeclarationNode
    ) {
        this( new SourceCodeMethodDescriptor(filePath, className, methodName), astMethodDeclarationNode);
    }

    public int getNcss() {
        return ncss;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }
}
