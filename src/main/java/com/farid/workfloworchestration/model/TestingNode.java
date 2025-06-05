package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * TestingNode
 *
 * Represents a node that evaluates the model on unseen test data.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link ExecutableNode}</li>
 *   <li><b>Method Overriding:</b> Provides specific behavior for testing execution</li>
 *   <li><b>Parametric Polymorphism (Generics):</b> Uses {@code ExecutableNode<String>}</li>
 *   <li><b>Encapsulation:</b> All internal logic is encapsulated inside method bodies</li>
 * </ul>
 */
public class TestingNode extends ExecutableNode<String> {

    /**
     * Constructor for a basic TestingNode with ID and name.
     */
    public TestingNode(String id, String name) {
        super(id, name, NodeType.TESTING);
    }

    /**
     * Constructor for a TestingNode with ID, name, and description.
     */
    public TestingNode(String id, String name, String description) {
        super(id, name, description, NodeType.TESTING);
    }

    /**
     * Executes the core logic of the testing node.
     * Outputs model evaluation on unseen test data.
     */
    @Override
    public void execute() {
        System.out.println("🧪 Testing node executed: " + getName());
        System.out.println("➡️ Evaluating model on held-out test data to measure generalization...");
    }

    /**
     * Validates the node configuration.
     * Currently always returns true, but can be extended to validate test data presence.
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the node using runtime context metadata.
     * Logs the operation with contextual awareness.
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🧾 Running testing with context: " + context + " for node: " + getName());
        executionLogger.log("TestingNode executed with context: " + context);
    }

    /**
     * Rejects any unsupported operations with a custom exception.
     *
     * @param operation Name of the attempted operation
     * @throws UnsupportedOperationForNodeException Always thrown for disallowed actions
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
