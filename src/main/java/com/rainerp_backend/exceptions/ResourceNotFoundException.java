package com.rainerp_backend.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " com ID " + id + " não foi encontrado.");
    }
}
