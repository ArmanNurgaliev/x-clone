package ru.arman.backendxclone.exception;

import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends Exception {
    private Map<String, String> errors;

    public ValidationException(Map<String, String> errors) {
        this.errors = errors;
    }
}
