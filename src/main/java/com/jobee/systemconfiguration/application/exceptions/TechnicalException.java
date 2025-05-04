package com.jobee.systemconfiguration.application.exceptions;

public class TechnicalException extends RuntimeException {

    public TechnicalException(String message, Throwable cause) {
        super(message, cause);
    }
}
