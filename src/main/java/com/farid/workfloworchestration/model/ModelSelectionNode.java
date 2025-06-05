package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code ModelSelectionNode} simulates the process of choosing the best model
 * based on a given evaluation criterion (e.g., accuracy, F1-score).
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits behavior from {@link ExecutableNode}</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li>Runtime: overrides {@code execute()} and {@code executeWithContext()}</li>
 *       <li>Parametric: uses generics via {@code <String>}</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> Internal logic and strategy are hidden through getters/setters</li>
 *   <li><b>Abstraction:</b> Implements abstract execution defined in superclass</li>
 * </ul>
 */
public class ModelSelectionNode extends ExecutableNode<String> {

    // Criteria used to select the best model (e.g., accuracy, precision)
    private String selectionCriteria;

    /**
     * Constructor with default selection strategy.
     * @param id   Unique node ID
     * @param name Node name
     */
    public ModelSelectionNode(String id, String name) {
        super(id, name, NodeType.MODEL_SELECTION);
        this.selectionCriteria = "accuracy";
    }

    /**
     * Constructor with custom selection criteria.
     * Used in NodeFactory (3-parameter version).
     * @param id                Node ID
     * @param name              Node name
     * @param selectionCriteria Selection strategy (e.g., accuracy, F1-score)
     */
    public ModelSelectionNode(String id, String name, String selectionCriteria) {
        super(id, name, NodeType.MODEL_SELECTION);
        this.selectionCriteria = selectionCriteria;
    }

    /**
     * Full constructor with description and criteria.
     * @param id                Node ID
     * @param name              Node name
     * @param description       Description of selection logic
     * @param selectionCriteria Selection criterion
     */
    public ModelSelectionNode(String id, String name, String description, String selectionCriteria) {
        super(id, name, description, NodeType.MODEL_SELECTION);
        this.selectionCriteria = selectionCriteria;
    }

    /**
     * Retrieves the model selection strategy.
     */
    public String getSelectionCriteria() {
        return selectionCriteria;
    }

    /**
     * Updates the model selection strategy.
     */
    public void setSelectionCriteria(String selectionCriteria) {
        this.selectionCriteria = selectionCriteria;
    }

    /**
     * Default execution logic for this node (used without context).
     */
    @Override
    public void execute() {
        System.out.println("🔍 Selecting best model using criteria: " + selectionCriteria);
        String selectedModel = "Model_X"; // Placeholder logic
        System.out.println("✅ Best model selected: " + selectedModel);
    }

    /**
     * Checks if this node is logically valid (i.e., it has a non-empty selection criterion).
     */
    @Override
    public boolean isValid() {
        return selectionCriteria != null && !selectionCriteria.trim().isEmpty();
    }

    /**
     * Execution logic that considers external runtime context.
     *
     * @param context Dynamic metadata passed to the node
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🔍 [With Context] Selecting model based on: " + selectionCriteria);
        System.out.println("📌 Context received: " + context);

        // Placeholder logic — in real scenario, models would be compared
        String result = "Selected best model with context using: " + selectionCriteria;
        executionLogger.log(result);
    }

    /**
     * Throws exception for unsupported operations on this node type.
     *
     * @param operation Name of the operation to validate
     * @throws UnsupportedOperationForNodeException Always, since operation is disallowed
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
