package com.devops.back.infra;

public class ExceptionInfrastructure extends RuntimeException {

    public ExceptionInfrastructure(String message) {
        super(message);
    }

    public ExceptionInfrastructure(String message, Throwable cause) {
        super(message, cause);
    }
}
