package com.github.emcat.metric_calculators;

public class MethodProcessingException extends RuntimeException {
    private final transient MethodDescriptor method;
    private final transient Exception exception;

    private static final long serialVersionUID = -2272341134842435255L;

    public MethodProcessingException(final MethodDescriptor method, final Exception exception) {
        super("Processing method:\n" + method + "\nFollowing error occurred:\n" + exception);
        this.method = method;
        this.exception = exception;
    }

    public MethodDescriptor getMethodDescriptor() {
        return method;
    }

    public Exception getException() {
        return exception;
    }
}
