package com.github.emcat.metric_calculators;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;
import org.apache.commons.lang3.NotImplementedException;

public final class MethodMetricsCalculationUtils {
    private final static CycloMetric CYCLOMATIC_COMPLEXITY_CALCULATOR = new CycloMetric();
    private final static NcssMetric.NcssOperationMetric NCSS_CALCULATOR = new NcssMetric.NcssOperationMetric();

    public static int calculateNcss(final ASTMethodOrConstructorDeclaration declarationNode) {
        return (int) NCSS_CALCULATOR.computeFor(declarationNode, MetricOptions.emptyOptions());
    }

    public static int calculateCyclomaticComplexityfinal(final ASTMethodOrConstructorDeclaration declarationNode) {
        return (int) CYCLOMATIC_COMPLEXITY_CALCULATOR.computeFor(declarationNode, MetricOptions.emptyOptions());
    }

    private MethodMetricsCalculationUtils() {
        throw new NotImplementedException();
    }
}
