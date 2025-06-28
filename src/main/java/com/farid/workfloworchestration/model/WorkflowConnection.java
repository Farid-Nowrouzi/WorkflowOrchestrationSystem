package com.farid.workfloworchestration.model;

/**
 * Represents a directional connection (or edge) between two workflow nodes in the graph.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Composition:</b> Connection is tightly coupled with the source node (auto-registration in constructor).</li>
 *   <li><b>Association:</b> Holds references to other objects (source and target nodes).</li>
 *   <li><b>Encapsulation:</b> Provides getter/setter methods to access or modify fields safely.</li>
 *   <li><b>Defensive Programming:</b> Prevents creation of invalid connections (null checks in constructor).</li>
 * </ul>
 */
public class WorkflowConnection {

    // === Private Fields (Information Hiding Compliance) ===

    // Mutable references — allow reassignment via setters
    private WorkflowNode sourceNode;
    private WorkflowNode targetNode;
    private String condition; // Optional condition label (e.g., "YES", "NO")

    /**
     * Constructor for unconditional connections.
     * Automatically registers the connection with the source node (Composition).
     *
     * @param sourceNode the source node (must not be null)
     * @param targetNode the target node (must not be null)
     */
    public WorkflowConnection(WorkflowNode sourceNode, WorkflowNode targetNode) {
        this(sourceNode, targetNode, null); // Delegate to main constructor
    }

    /**
     * Constructor for connections with optional condition labels.
     * Enforces non-null references for both nodes.
     *
     * @param sourceNode source of the connection
     * @param targetNode destination of the connection
     * @param condition optional condition string (can be null)
     */
    public WorkflowConnection(WorkflowNode sourceNode, WorkflowNode targetNode, String condition) {
        if (sourceNode == null || targetNode == null) {
            throw new IllegalArgumentException("Source and Target nodes cannot be null");
        }

        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.condition = (condition != null && !condition.isEmpty()) ? condition : null;

        //  Composition: Register this connection in the source node's outgoing connections
        this.sourceNode.addOutgoingConnection(this);
    }

    // === Getters and Setters (Encapsulation) ===

    public WorkflowNode getSourceNode() {
        return sourceNode;
    }

    public void setSourceNode(WorkflowNode sourceNode) {
        this.sourceNode = sourceNode;
    }

    public WorkflowNode getTargetNode() {
        return targetNode;
    }

    public void setTargetNode(WorkflowNode targetNode) {
        this.targetNode = targetNode;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = (condition != null && !condition.isBlank()) ? condition : null;
    }

    /**
     * Returns true if this connection has a non-null condition label.
     */
    public boolean hasCondition() {
        return condition != null;
    }

    /**
     * String representation of the connection, used in logs or debugging.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Connection from ")
                .append(sourceNode.getName())
                .append(" to ")
                .append(targetNode.getName());

        if (condition != null) {
            sb.append(" [Condition: ").append(condition).append("]");
        }

        return sb.toString();
    }
}
