package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * ValidationNode
 *
 * Represents a node responsible for validating a trained machine learning model
 * using a separate validation set to assess performance and generalization.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from ExecutableNode&lt;String&gt; for reusability and abstraction.</li>
 *   <li><b>Overriding:</b> Overrides execution behavior to specify validation-specific logic.</li>
 *   <li><b>Parametric Polymorphism (Generics):</b> Inherits generic behavior while specializing for String output.</li>
 *   <li><b>Abstraction:</b> Hides internal logic of validation behind an interface-based structure.</li>
 * </ul>
 */
public class ValidationNode extends ExecutableNode<String> {

    /**
     * Constructor with ID and name only (no description).
     */
    public ValidationNode(String id, String name) {
        super(id, name, NodeType.VALIDATION);
    }

    /**
     * Constructor with ID, name, and custom description.
     */
    public ValidationNode(String id, String name, String description) {
        super(id, name, description, NodeType.VALIDATION);
    }

    /**
     * Basic execution logic without external context.
     * Demonstrates overriding of base class behavior.
     */
    @Override
    public void execute() {
        System.out.println("✅ Validation node executed: " + getName());
        System.out.println("🧪 Validating model on hold-out dataset... (checking overfitting, generalization, etc.)");
    }

    /**
     * Validation logic with dynamic context input.
     * Useful when passing external runtime information into the node.
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🔍 Validation with context: " + context + " for node: " + getName());
        executionLogger.log("ValidationNode executed with context: " + context);
    }

    /**
     * Runtime check to determine if the node is valid for execution.
     * Can be extended with more domain-specific checks.
     */
    @Override
    public boolean isValid() {
        return true; // You can add validation rules such as input presence or preconditions.
    }

    /**
     * Fallback: disallows arbitrary operations unless explicitly defined.
     * Supports robustness and safe usage.
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName()
        );
    }
}
