package com.github.emcat.metric_calculators;

public class MethodProcessingException extends RuntimeException {
    public MethodProcessingException(MethodDescriptor method, Exception exception) {
        this.method = method;
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "Processing method:\n" + method + "\nFollowing error occurred:\n" + exception;

    }

    public MethodDescriptor getMethodDescriptor() {
        return method;
    }

    public Exception getException() {
        return exception;
    }

    private MethodDescriptor method;
    private Exception exception;
}
