package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.execution.GenericExecutionLogger;

import java.util.HashMap;
import java.util.Map;

/**
 * {@code ExecutableNode<T>} is an abstract base class for all workflow nodes
 * that involve runtime execution logic with flexible result types.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link WorkflowNode}</li>
 *   <li><b>Abstraction:</b> Declares {@code executeWithContext()} as abstract</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li><i>Parametric (Generics):</i> {@code <T>} allows varying result types</li>
 *       <li><i>Ad-hoc:</i> Overloaded {@code execute(...)} methods</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> Internal metadata and parameters are accessed via controlled methods</li>
 *   <li><b>Modularity:</b> Promotes reusable execution logic for all executable nodes</li>
 * </ul>
 *
 * <p>This design forms the foundation for various specialized nodes such as
 * {@code PredictionNode}, {@code ConditionNode}, {@code ClusteringNode}, and more.</p>
 *
 * @param <T> The generic type representing the result/output of the execution
 */
public abstract class ExecutableNode<T> extends WorkflowNode implements Executable {

    /**
     * Execution parameters relevant to this node (e.g., "batchSize" → "32").
     * This field is encapsulated and modifiable via provided methods.
     */
    protected Map<String, String> executionParameters;

    /**
     * A generic logger to store and track node-specific results.
     * Demonstrates use of parametric polymorphism.
     */
    protected GenericExecutionLogger<T> executionLogger = new GenericExecutionLogger<>();

    /**
     * Metadata used during execution, allowing context-sensitive behavior.
     * Enables parametric polymorphism with dynamic runtime values.
     */
    private Map<String, String> metadata = new HashMap<>();

    // ======== Constructors ========

    /**
     * Basic constructor without description.
     *
     * @param id        Node identifier
     * @param name      Node display name
     * @param nodeType  Enum type of the node
     */
    public ExecutableNode(String id, String name, NodeType nodeType) {
        super(id, name, nodeType);
        this.executionParameters = new HashMap<>();
    }

    /**
     * Extended constructor with description.
     *
     * @param id          Node identifier
     * @param name        Node display name
     * @param description Human-readable summary of the node's purpose
     * @param nodeType    Enum type of the node
     */
    public ExecutableNode(String id, String name, String description, NodeType nodeType) {
        super(id, name, description, nodeType);
        this.executionParameters = new HashMap<>();
    }

    // ======== Encapsulated Access to Parameters ========

    /**
     * Returns the current execution parameters.
     */
    public Map<String, String> getExecutionParameters() {
        return executionParameters;
    }

    /**
     * Sets or updates an execution parameter.
     *
     * @param key   Parameter key (e.g., "model")
     * @param value Parameter value (e.g., "XGBoost")
     */
    public void setExecutionParameter(String key, String value) {
        this.executionParameters.put(key, value);
    }

    /**
     * Clears all execution parameters.
     */
    public void clearExecutionParameters() {
        this.executionParameters.clear();
    }

    /**
     * Returns the generic execution logger.
     */
    public GenericExecutionLogger<T> getExecutionLogger() {
        return executionLogger;
    }

    // ======== Metadata (for Runtime Context Injection) ========

    /**
     * Gets execution metadata, which may affect runtime logic.
     */
    public Map<String, String> getMetadata() {
        return metadata;
    }

    /**
     * Updates the runtime metadata used for execution.
     */
    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    // ======== Execution Methods (Overridable/Abstract) ========

    /**
     * Abstract method to be implemented by subclasses.
     * Executes the node using provided key-value context.
     *
     * @param context Execution metadata map (can be null)
     */
    public abstract void executeWithContext(Map<String, String> context);

    /**
     * Base execution entry point (no context). Delegates to {@code executeWithContext(null)}.
     */
    @Override
    public void execute() {
        executeWithContext(null);
    }

    /**
     * Ad-hoc polymorphism: Overloaded version with label annotation.
     * Used for debug, tracing, or UI-labeled execution runs.
     *
     * @param label A string label attached to this execution (e.g., "demo", "run1")
     */
    public void execute(String label) {
        System.out.println("🧠 Executing node [" + getName() + "] with label: " + label);
        executeWithContext(null);
    }
}
