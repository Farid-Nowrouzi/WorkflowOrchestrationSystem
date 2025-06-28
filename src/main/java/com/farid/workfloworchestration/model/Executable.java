package com.farid.workfloworchestration.model;

import java.util.Map;

// === Information Hiding Compliance ===
// This interface defines no attributes.
// It declares abstract methods only, as expected for modular contracts.

/**
 * {@code Executable} is a functional interface representing any component
 * in the workflow that supports runtime execution.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Abstraction</b>: Defines the contract for executing nodes without specifying how</li>
 *   <li><b>Interface</b>: Enables multiple node types to implement their own custom logic</li>
 *   <li><b>Polymorphism</b>: Supports dynamic dispatch via method overriding</li>
 * </ul>
 *
 * <p>This abstraction promotes modularity, testability, and future extensibility
 * of the workflow orchestration system.</p>
 *
 * <p>Implemented by classes such as:
 * {@link ExecutableNode}, {@link ConditionNode}, {@link ClusteringNode}, etc.</p>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public interface Executable {

    /**
     * Executes the node's default behavior.
     * Typically called when no specific context is required.
     *
     * <p><b>Example:</b> Running a preprocessing step or analysis module.</p>
     */
    void execute();

    /**
     * Executes the node with a supplied metadata context.
     * Enables dynamic, data-driven behavior.
     *
     * <p><b>Use Case:</b> ConditionNode evaluating expressions based on runtime parameters.</p>
     *
     * @param context A key-value map of parameters influencing the execution behavior
     */
    void executeWithContext(Map<String, String> context);
}
