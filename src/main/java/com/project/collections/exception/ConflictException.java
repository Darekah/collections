package com.project.collections.exception;

/**
 * Thrown when a request conflicts with the current state of the resource
 * (e.g. a unique identifier already assigned to a different entity).
 */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }

}
