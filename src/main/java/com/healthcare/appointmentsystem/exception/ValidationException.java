package com.healthcare.appointmentsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    private final Map<String, String> errors = new HashMap<>();
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors.putAll(errors);
    }
    
    public Map<String, String> getErrors() {
        return errors;
    }
    
    public void addError(String field, String message) {
        errors.put(field, message);
    }
}
