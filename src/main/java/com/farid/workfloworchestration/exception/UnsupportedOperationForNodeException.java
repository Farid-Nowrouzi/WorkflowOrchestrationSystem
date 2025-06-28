package com.farid.workfloworchestration.exception;

/**
 * Exception thrown when a node does not support a requested operation.
 *
 * <p>This exception is typically used to indicate a misuse of node functionality,
 * such as attempting to run a non-executable node or invoking behavior
 * not compatible with the node’s type.</p>
 *
 * <p>This promotes robust error handling by clearly signaling unsupported
 * operations at runtime, allowing for graceful recovery or user feedback.</p>
 *
 * <p>Extends {@link Exception}, making it a checked exception that must be
 * handled explicitly.</p>
 *
 * Example usage:
 * <pre>
 *     if (!(node instanceof ExecutableNode)) {
 *         throw new UnsupportedOperationForNodeException("This node cannot be executed.");
 *     }
 * </pre>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
// === Information Hiding Compliance ===
// No fields exposed; uses constructor-based encapsulation via inheritance.

public class UnsupportedOperationForNodeException extends Exception { // Inheritance, Subtyping

  /**
   * Constructs a new exception with a specific error message.
   *
   * @param message A message describing the unsupported operation.
   */
  public UnsupportedOperationForNodeException(String message) {
    super(message); // Inheritance: passes message to base Exception class
  }

  /**
   * Constructs a new exception with a message and underlying cause.
   *
   * @param message Description of the unsupported operation
   * @param cause   Underlying cause of the exception
   */
  public UnsupportedOperationForNodeException(String message, Throwable cause) {
    super(message, cause); // Inheritance: supports exception chaining
  }
}
