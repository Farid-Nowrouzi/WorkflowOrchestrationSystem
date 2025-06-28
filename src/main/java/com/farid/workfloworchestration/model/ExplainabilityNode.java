package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code ExplainabilityNode} represents a node responsible for generating
 * explanations for model outputs (e.g., feature attributions, SHAP values).
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Extends {@link ExecutableNode} for shared execution logic</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li>Runtime polymorphism via method overriding</li>
 *       <li>Generic typing with {@code <String>} as the execution result</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> Execution logic and internal state hidden from other components</li>
 *   <li><b>Abstraction:</b> Uses abstract behavior defined in {@code ExecutableNode}</li>
 * </ul>
 *
 * <p>This node is commonly used to interpret machine learning models
 * after prediction, enhancing transparency and trust in black-box systems.</p>
 */
public class ExplainabilityNode extends ExecutableNode<String> {

    // === Information Hiding Compliance ===
// This class defines no new attributes.
// All encapsulated fields are inherited from ExecutableNode<String>.


    /**
     * Constructor for a basic ExplainabilityNode without description.
     *
     * @param id   Unique node identifier
     * @param name Display name shown in the UI
     */
    public ExplainabilityNode(String id, String name) {
        super(id, name, NodeType.EXPLAINABILITY);
    }

    /**
     * Extended constructor with description.
     *
     * @param id          Unique node identifier
     * @param name        Display name
     * @param description A summary of the explainability purpose
     */
    public ExplainabilityNode(String id, String name, String description) {
        super(id, name, description, NodeType.EXPLAINABILITY);
    }

    /**
     * Executes the node with no additional context.
     * Polymorphic behavior: overrides base class method.
     */
    @Override
    public void execute() {
        System.out.println(" Explainability node executed: generating feature attributions for model prediction...");
    }

    /**
     * Validates whether this node is correctly configured.
     * Currently always returns {@code true}, but can be extended.
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the node with runtime metadata (context).
     * Demonstrates parametric polymorphism via Map<String, String>.
     *
     * @param context Key-value metadata for context-aware execution
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println(" Explainability node executed with context: " + context);

        //  Log output to generic execution logger
        executionLogger.log("ExplainabilityNode executed with context: " + context);
    }

    /**
     * Prevents unsupported operations from being called.
     * Demonstrates defensive programming and custom exception usage.
     *
     * @param operation Name of the operation being validated
     * @throws UnsupportedOperationForNodeException Always thrown in this implementation
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
