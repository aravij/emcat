package com.github.emcat.metric_calculators;

import com.opencsv.bean.CsvBindByName;

@SuppressWarnings("PMD.DataClass")
public class MethodMetrics extends MethodDescriptor {
    @CsvBindByName(column = "NCSS")
    private int ncss;

    @CsvBindByName(column = "CYCLOMATIC_COMPLEXITY")
    private int cyclomaticComplexity;

    public MethodMetrics(
        final String filePath,
        final String className,
        final String methodName,
        final int ncss,
        final int cyclomaticComplexity
    ) {
        super(filePath, className, methodName);
        this.ncss = ncss;
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public MethodMetrics() {
        this("", "", "", 0, 0);
    }

    public int getNcss() {
        return ncss;
    }

    public void setNcss(final int ncss) {
        this.ncss = ncss;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(final int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }
}
