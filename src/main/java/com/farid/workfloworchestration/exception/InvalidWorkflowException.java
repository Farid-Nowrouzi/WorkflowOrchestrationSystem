package com.farid.workfloworchestration.exception;

/**
 * Custom exception used to indicate an invalid or malformed workflow configuration.
 *
 * <p>This exception may be thrown when the system detects logical errors
 * such as missing connections, invalid node sequences, or cyclic dependencies.</p>
 *
 * <p>Extends {@link Exception}, making it a checked exception, and provides
 * multiple constructors to support different error reporting use cases.</p>
 *
 * Example usage:
 * <pre>
 *     if (!isWorkflowValid()) {
 *         throw new InvalidWorkflowException("Workflow is incomplete or has errors.");
 *     }
 * </pre>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
// === Information Hiding Compliance ===
// No public fields. All exception data is handled internally via the Exception superclass.

public class InvalidWorkflowException extends Exception { // Inheritance, Subtyping

  /**
   * Constructs a new exception with a specific message.
   *
   * @param message Explanation of the workflow issue.
   */
  public InvalidWorkflowException(String message) {
    super(message); // Inheritance: calls constructor of the base class
  }

  /**
   * Constructs a new exception with a message and a cause.
   *
   * @param message Description of the error
   * @param cause The underlying throwable that caused this exception
   */
  public InvalidWorkflowException(String message, Throwable cause) {
    super(message, cause); // Inheritance: exception chaining support
  }

  /**
   * Constructs a new exception with a default message.
   */
  public InvalidWorkflowException() {
    super("Invalid workflow encountered."); // Encapsulation, reuse of base behavior
  }
}
