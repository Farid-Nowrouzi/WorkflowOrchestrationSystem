package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code FeatureEngineeringNode} represents a stage in the ML pipeline responsible
 * for transforming raw data into meaningful features for model training.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Extends {@link ExecutableNode} to reuse execution-related logic</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li>Runtime polymorphism via overridden methods</li>
 *       <li>Parametric polymorphism through the generic type {@code <String>}</li>
 *     </ul>
 *   </li>
 *   <li><b>Abstraction:</b> Implements abstract methods from base class</li>
 *   <li><b>Encapsulation:</b> Execution details and logging are abstracted away from external classes</li>
 * </ul>
 *
 * <p>This node is commonly used in workflows that prepare datasets for training by encoding, normalizing,
 * or creating new derived features from existing data.</p>
 */
public class FeatureEngineeringNode extends ExecutableNode<String> {

    /**
     * Basic constructor without description.
     *
     * @param id   Unique identifier of the node
     * @param name Display name for visualization
     */
    public FeatureEngineeringNode(String id, String name) {
        super(id, name, NodeType.FEATURE_ENGINEERING);
    }

    /**
     * Extended constructor with a user-defined description.
     *
     * @param id          Unique identifier of the node
     * @param name        Display name
     * @param description Summary of what this node does
     */
    public FeatureEngineeringNode(String id, String name, String description) {
        super(id, name, description, NodeType.FEATURE_ENGINEERING);
    }

    /**
     * Executes the feature engineering logic without context.
     * Used in general workflows or test executions.
     */
    @Override
    public void execute() {
        System.out.println("🔧 Feature Engineering node executed: creating and transforming features...");
    }

    /**
     * Validates the configuration of this node.
     *
     * @return {@code true} by default; can be extended with field checks
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the node with runtime metadata for dynamic behavior.
     *
     * @param context Key-value metadata passed by the controller
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🔧 Feature Engineering node executed with context: " + context);

        // ✅ Log execution with generic logger
        executionLogger.log("FeatureEngineeringNode executed with context: " + context);
    }

    /**
     * Prevents unsupported operations from being performed on this node type.
     *
     * @param operation The name of the requested operation
     * @throws UnsupportedOperationForNodeException Always thrown for this node
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
