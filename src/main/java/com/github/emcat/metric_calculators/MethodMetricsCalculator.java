package com.github.emcat.metric_calculators;

import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric;
import net.sourceforge.pmd.lang.java.metrics.impl.NcssMetric;
import net.sourceforge.pmd.lang.metrics.MetricOptions;

public class MethodMetricsCalculator {
    private final transient CycloMetric cyclomaticComplexityCalculator = new CycloMetric();
    private final transient NcssMetric.NcssOperationMetric ncssCalculator = new NcssMetric.NcssOperationMetric();

    public MethodMetricsData calculateMethodMetrics(final ASTMethodOrConstructorDeclaration declarationNode) {
        return new MethodMetricsData(
            (int) ncssCalculator.computeFor(declarationNode, MetricOptions.emptyOptions()),
            (int) cyclomaticComplexityCalculator.computeFor(declarationNode, MetricOptions.emptyOptions())
        );
    }
}
