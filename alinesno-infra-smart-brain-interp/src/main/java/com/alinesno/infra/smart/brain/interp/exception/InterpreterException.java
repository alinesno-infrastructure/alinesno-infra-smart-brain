package com.alinesno.infra.smart.brain.interp.exception;

public class InterpreterException extends Exception {
    public InterpreterException(String message) {
        super(message);
    }

    public InterpreterException(String message, Throwable cause) {
        super(message, cause);
    }
}
