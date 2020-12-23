package com.github.emcat;

import com.opencsv.bean.CsvBindByName;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

import java.io.IOException;

// TODO: Add line number to support overloaded methods and constructors
// TODO: Add text range as extraction opportunity
@SuppressWarnings("PMD.DataClass")
public class SourceCodeMethod {
    @CsvBindByName(column = "FILE_PATH")
    private String filePath;

    @CsvBindByName(column = "CLASS_NAME")
    private String className;

    @CsvBindByName(column = "METHOD_NAME")
    private String methodName;

    @CsvBindByName(column = "NCSS")
    private transient int ncss;

    @CsvBindByName(column = "CYCLOMATIC_COMPLEXITY")
    private transient int cyclomaticComplexity;

    private transient ASTMethodDeclaration astMethodDeclaration;

    private final static AstMethodFinder AST_METHOD_FINDER = new AstMethodFinder();

    private final static CycloMetric CYCLOMATIC_COMPLEXITY_CALCULATOR = new CycloMetric();

    private final static NcssMetric.NcssOperationMetric NCSS_CALCULATOR = new NcssMetric.NcssOperationMetric();

    private final static int METRICS_NOT_CALCULATED = -1;

    public SourceCodeMethod(
            final String filePath,
            final String className,
            final String methodName
    ) {
        this.filePath = filePath;
        this.className = className;
        this.methodName = methodName;
        this.ncss = METRICS_NOT_CALCULATED;
        this.cyclomaticComplexity = METRICS_NOT_CALCULATED;
    }

    public SourceCodeMethod() {
        this("", "", "");
    }

    public void calculateMetrics() throws IOException {
        calculateNcss();
        calculateCyclomaticComplexity();
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(final String methodName) {
        this.methodName = methodName;
    }

    public int getNcss() {
        return ncss;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    private void findAstNode() throws IOException {
        if (astMethodDeclaration == null) {
            astMethodDeclaration = AST_METHOD_FINDER.findAstMethod(filePath, className, methodName);
        }
    }

    private void calculateNcss() throws IOException {
        findAstNode();
        if (ncss == METRICS_NOT_CALCULATED) {
            ncss = (int) NCSS_CALCULATOR.computeFor(astMethodDeclaration, MetricOptions.emptyOptions());
        }
    }

    private void calculateCyclomaticComplexity() throws IOException {
        findAstNode();
        if (cyclomaticComplexity == METRICS_NOT_CALCULATED) {
            cyclomaticComplexity = (int) CYCLOMATIC_COMPLEXITY_CALCULATOR.computeFor(
                astMethodDeclaration,
                MetricOptions.emptyOptions()
            );
        }
    }
}
