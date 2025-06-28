package com.farid.workfloworchestration.exception;

/**
 * Custom exception used to indicate invalid or malformed metadata in the workflow.
 *
 * <p>This exception is thrown when a node or process encounters invalid input
 * or configuration metadata that prevents it from functioning properly.</p>
 *
 * <p>Extends {@link Exception}, making it a checked exception that must be
 * handled explicitly using try-catch blocks or declared in method signatures.</p>
 *
 * This promotes clarity, safety, and robust error handling in the orchestration system.
 *
 * Example usage:
 * <pre>
 *     if (!metadataIsValid()) {
 *         throw new InvalidMetadataException("Metadata format is invalid.");
 *     }
 * </pre>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
// === Information Hiding Compliance ===
// No public fields. All internal state is handled by the Exception superclass.

public class InvalidMetadataException extends Exception { // Inheritance, Subtyping

    /**
     * Constructs a new InvalidMetadataException with a descriptive error message.
     *
     * @param message Detailed explanation of the error cause.
     */
    public InvalidMetadataException(String message) {
        super(message); // Inheritance: calling superclass constructor
    }
}
