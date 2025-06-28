package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code EnsembleNode} represents a step in the workflow where multiple models
 * are combined using an ensemble strategy (e.g., voting, stacking).
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance</b>: Inherits from {@code ExecutableNode<String>}</li>
 *   <li><b>Polymorphism</b>: Method overriding for {@code execute()}, {@code executeWithContext()}, etc.</li>
 *   <li><b>Encapsulation</b>: {@code ensembleStrategy} field is private with getters/setters</li>
 *   <li><b>Abstraction</b>: Complex ensemble logic hidden behind simple {@code execute()} calls</li>
 *   <li><b>Generics (Parametric Polymorphism)</b>: Inherits {@code ExecutableNode<String>} for flexible logging</li>
 * </ul>
 *
 * This node is essential in ensemble learning workflows where combining predictions
 * improves overall accuracy and robustness.
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class EnsembleNode extends ExecutableNode<String> {

    // === Private Field (Information Hiding Compliance) ===
// Mutable instance field for specifying the ensemble strategy (e.g., voting, stacking)
    private String ensembleStrategy;

    // === Information Hiding Compliance ===
// This class defines one mutable instance attribute: ensembleStrategy.
// All other attributes are inherited from ExecutableNode<String>.



    /**
     * Constructs an EnsembleNode with a specific strategy.
     *
     * @param id                The unique identifier for this node.
     * @param name              The display name of the node.
     * @param ensembleStrategy  The ensemble strategy (e.g., "Voting", "Stacking").
     */
    public EnsembleNode(String id, String name, String ensembleStrategy) {
        super(id, name, NodeType.ENSEMBLE); // Inheritance + Enum
        this.ensembleStrategy = ensembleStrategy;
    }

    /**
     * Constructs an EnsembleNode with a default strategy.
     *
     * @param id    The node ID.
     * @param name  The node name.
     */
    public EnsembleNode(String id, String name) {
        super(id, name, NodeType.ENSEMBLE);
        this.ensembleStrategy = "Default strategy"; // Default fallback
    }

    // === Encapsulation (Getters/Setters) ===

    public String getEnsembleStrategy() {
        return ensembleStrategy;
    }

    public void setEnsembleStrategy(String ensembleStrategy) {
        this.ensembleStrategy = ensembleStrategy;
    }

    // === Polymorphism (Overridden Execution) ===

    /**
     * Executes the ensemble strategy without additional context.
     */
    @Override
    public void execute() {
        System.out.println(" Executing ensemble node: " + getName() +
                " using strategy: " + ensembleStrategy);
    }

    /**
     * Executes the ensemble strategy with additional contextual metadata.
     *
     * @param context A key-value map representing external inputs or configuration.
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println(" Ensemble node context execution: " +
                context + " | Strategy: " + ensembleStrategy);

        // Log the result using generic logger (parametric polymorphism)
        executionLogger.log("EnsembleNode executed with strategy: " +
                ensembleStrategy + " and context: " + context);
    }

    /**
     * Validates that the ensemble node has a valid strategy.
     *
     * @return true if the strategy is non-null and non-empty.
     */
    @Override
    public boolean isValid() {
        return ensembleStrategy != null && !ensembleStrategy.trim().isEmpty();
    }

    /**
     * Validates whether a requested operation is allowed on this node.
     *
     * @param operation The name of the operation to validate.
     * @throws UnsupportedOperationForNodeException Always thrown for unsupported actions.
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName());
    }
}
