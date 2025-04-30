package com.farid.workfloworchestration.exception;

/**
 * Custom exception to handle invalid workflow operations.
 *
 * OOP Concepts Implemented:
 * - Inheritance: Extends Exception class.
 * - Abstraction: Hides complex exception details.
 * - Encapsulation: Private message field (inside Exception class).
 * - Information Hiding: Only exposing the necessary details.
 */
public class InvalidWorkflowException extends Exception {

    /**
     * Constructor to create an InvalidWorkflowException with a custom message.
     *
     * @param message Detailed message about the invalid operation.
     */
    public InvalidWorkflowException(String message) {
        super(message); // Inherits behavior from Exception
    }

    /**
     * Constructor to create an InvalidWorkflowException with a custom message and cause.
     *
     * @param message Detailed message about the invalid operation.
     * @param cause The original cause of the exception.
     */
    public InvalidWorkflowException(String message, Throwable cause) {
        super(message, cause);
    }
}
