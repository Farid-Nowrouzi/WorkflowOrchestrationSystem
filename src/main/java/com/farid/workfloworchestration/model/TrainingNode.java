package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * TrainingNode
 *
 * Represents a node that performs machine learning model training.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Extends from {@link ExecutableNode}</li>
 *   <li><b>Method Overriding:</b> Custom behavior for {@code execute()}, {@code executeWithContext()}, {@code validateOperation()}</li>
 *   <li><b>Generic Polymorphism:</b> Declared as {@code ExecutableNode<String>} to log string-based output</li>
 *   <li><b>Encapsulation:</b> Uses protected {@code executionLogger} from superclass to hide internal logging logic</li>
 *   <li><b>Coercion Polymorphism:</b> Demonstrates implicit type conversion (e.g., {@code double} and {@code int} to {@code String})</li>
 * </ul>
 */
public class TrainingNode extends ExecutableNode<String> {

    /**
     * Constructor with ID and name.
     */
    public TrainingNode(String id, String name) {
        super(id, name, NodeType.TRAINING);
    }

    /**
     * Constructor with ID, name, and description.
     */
    public TrainingNode(String id, String name, String description) {
        super(id, name, description, NodeType.TRAINING);
    }

    /**
     * Executes the training logic (no context).
     * Logs progress with implicit type conversion.
     */
    @Override
    public void execute() {
        System.out.println("🏋️ Training node executed: " + getName());
        System.out.println("📈 Model training in progress... (loading data, optimizing weights, etc.)");

        // ✅ Logging basic execution
        executionLogger.log("TrainingNode basic execution for: " + getName());

        // ✅ Coercion Polymorphism: Implicit conversion to String
        int epochCount = 5;
        double loss = 0.134;

        executionLogger.log("Epochs: " + epochCount);      // int → String
        executionLogger.log("Final loss: " + loss);        // double → String
    }

    /**
     * Validates the configuration of the node.
     * Always valid for now.
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the training node using external metadata (context).
     * Logs contextual parameters.
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🧠 Training with context: " + context + " for node: " + getName());

        // ✅ Context-aware logging with a tagged message
        executionLogger.logWithTag("TrainingNode executed with context: " + context, "TRAINING");

        // ✅ Coercion Polymorphism
        double accuracy = 0.92;
        executionLogger.log("Accuracy: " + accuracy);  // double → String
    }

    /**
     * Only the "train" operation is supported by this node.
     * Any other operation results in an exception.
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        if (!operation.equalsIgnoreCase("train")) {
            throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by TrainingNode.");
        }
    }
}
