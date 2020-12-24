package com.github.emcat;

import com.opencsv.bean.CsvBindByName;

// TODO: Add line number to support overloaded methods and constructors
// TODO: Add text range as extraction opportunity
@SuppressWarnings("PMD.DataClass")
public class SourceCodeMethodDescriptor {
    @CsvBindByName(column = "FILE_PATH")
    private String filePath;

    @CsvBindByName(column = "CLASS_NAME")
    private String className;

    @CsvBindByName(column = "METHOD_NAME")
    private String methodName;

    public SourceCodeMethodDescriptor(
        final String filePath,
        final String className,
        final String methodName
    ) {
        this.filePath = filePath;
        this.className = className;
        this.methodName = methodName;
    }

    public  SourceCodeMethodDescriptor() {
        this("", "", "");
    }

    public SourceCodeMethodDescriptor(final SourceCodeMethodDescriptor sourceCodeMethodDescriptor) {
        this.filePath = sourceCodeMethodDescriptor.getFilePath();
        this.className = sourceCodeMethodDescriptor.getClassName();
        this.methodName = sourceCodeMethodDescriptor.getMethodName();
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
}
