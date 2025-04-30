package com.farid.workfloworchestration.model;

/**
 * Represents a connection (edge) between two WorkflowNodes.
 * Demonstrates: Composition, Association, Defensive Programming
 */
public class WorkflowConnection {

    private WorkflowNode sourceNode;
    private WorkflowNode targetNode;
    private String condition; // Optional (e.g., "True", "False", or custom)

    // Constructor without condition
    public WorkflowConnection(WorkflowNode sourceNode, WorkflowNode targetNode) {
        this(sourceNode, targetNode, null);
    }

    // Constructor with optional condition
    public WorkflowConnection(WorkflowNode sourceNode, WorkflowNode targetNode, String condition) {
        if (sourceNode == null || targetNode == null) {
            throw new IllegalArgumentException("Source and Target nodes cannot be null");
        }

        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.condition = (condition != null && !condition.isEmpty()) ? condition : null;

        // 🌟 Automatically register the connection with the source node (Composition)
        this.sourceNode.addOutgoingConnection(this);
    }

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

    // 🌟 Helper method for UI logic
    public boolean hasCondition() {
        return condition != null;
    }

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
