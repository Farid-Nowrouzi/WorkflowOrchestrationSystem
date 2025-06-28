package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * Represents a node that performs clustering as part of a machine learning workflow.
 *
 * <p>Extends {@link ExecutableNode} and uses generic execution with type {@code String},
 * where the execution result is a descriptive summary of the clustering task.</p>
 *
 * <p>Supports context-aware execution via dataset injection, and logs results
 * using the generic logger inherited from the superclass.</p>
 *
 * Demonstrates:
 * <ul>
 *     <li>Inheritance and class hierarchy</li>
 *     <li>Polymorphism (method overriding, generics)</li>
 *     <li>Encapsulation (internal fields and validation)</li>
 *     <li>Custom exception signaling for unsupported operations</li>
 * </ul>
 *
 * Example:
 * <pre>
 *     ClusteringNode node = new ClusteringNode("n1", "Cluster1", "K-Means");
 *     node.execute(); // Prints and logs clustering summary
 * </pre>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class ClusteringNode extends ExecutableNode<String> { // Inheritance, Parametric Polymorphism

    // === Private Field (Information Hiding Compliance) ===
// Mutable instance field defining the clustering algorithm (e.g., "K-Means", "DBSCAN")
    private String method;

    // === Information Hiding Compliance ===
// This class defines one mutable private field: method.
// All other internal state is inherited from ExecutableNode<String> and encapsulated there.

    /**
     * Constructs a clustering node with an empty clustering method.
     *
     * @param id Node identifier
     * @param name Node name
     */
    public ClusteringNode(String id, String name) {
        super(id, name, NodeType.CLUSTERING); // Constructor chaining
        this.method = "";
    }

    /**
     * Constructs a clustering node with a specified clustering method.
     *
     * @param id Node identifier
     * @param name Node name
     * @param method The clustering algorithm to apply
     */
    public ClusteringNode(String id, String name, String method) {
        super(id, name, NodeType.CLUSTERING);
        this.method = method;
    }

    /**
     * Returns the clustering method assigned to this node.
     *
     * @return method used for clustering
     */
    public String getMethod() { // Encapsulation via getter
        return method;
    }

    /**
     * Sets the clustering method to be used.
     *
     * @param method Clustering algorithm (e.g., "K-Means", "DBSCAN")
     */
    public void setMethod(String method) { // Encapsulation via setter
        this.method = method;
    }

    /**
     * Executes the node using a default context.
     */
    @Override
    public void execute() { // Subtyping Polymorphism
        executeWithContext(null); // Delegates to context-aware logic
    }

    /**
     * Validates whether the clustering node has a defined method.
     *
     * @return true if the method is set
     */
    @Override
    public boolean isValid() {
        return method != null && !method.isBlank(); // Encapsulation + Abstraction
    }

    /**
     * Executes the clustering task using an optional input context.
     * Logs the clustering result both internally and on the terminal.
     *
     * @param context A map containing execution metadata (e.g., dataset name)
     */
    @Override
    public void executeWithContext(Map<String, String> context) { // Subtyping Polymorphism
        String dataset = (context != null && context.containsKey("dataset"))
                ? context.get("dataset")
                : "Unknown Dataset";

        String result = " Clustering applied on: " + dataset + " using method: " + method;

        // Composition: uses a logger object inherited from superclass
        executionLogger.log(result); // Code Reuse, Abstraction

        // Side effect: outputs execution result to terminal
        System.out.println(result);
    }

    /**
     * Throws an exception when any external operation is attempted.
     *
     * @param operation Name of the unsupported operation
     * @throws UnsupportedOperationForNodeException always
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        // Exception signaling for unsupported actions
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
