package com.farid.workfloworchestration.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import com.farid.workfloworchestration.exception.InvalidWorkflowException;
import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

/**
 * Abstract base class for all node types in the workflow system.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Encapsulation:</b> All internal state is private and accessed via getters/setters.</li>
 *   <li><b>Abstraction:</b> Defines abstract methods like {@code execute()} and {@code isValid()} for specialization.</li>
 *   <li><b>Inheritance:</b> All concrete node types extend this class (e.g., TaskNode, ConditionNode).</li>
 *   <li><b>Code Reuse:</b> Shared metadata, connection management, and validation logic are centralized here.</li>
 *   <li><b>Modularity:</b> Promotes reusable components in a large system architecture.</li>
 *   <li><b>Overloading:</b> Demonstrated in {@code addMetadata()} method with multiple signatures.</li>
 *   <li><b>Exception Handling:</b> Uses custom checked exceptions for validation and unsupported operations.</li>
 * </ul>
 */
public abstract class WorkflowNode implements Describable {

    // === Private Fields (Encapsulation) ===

    private String id;
    private String name;
    private String description;
    private NodeType nodeType;
    private Map<String, String> metadata;
    private LocalDateTime createdAt;
    private String details = "";

    private final List<WorkflowConnection> outgoingConnections = new ArrayList<>();
    private String colorHex = "#ffffff"; // Optional future customization

    // === Constructors (Overloading + Encapsulation) ===

    public WorkflowNode(String id, String name, NodeType nodeType) {
        this.id = id;
        this.name = name;
        this.nodeType = nodeType;
        this.metadata = new HashMap<>();
        this.createdAt = LocalDateTime.now();
    }

    public WorkflowNode(String id, String name, String description, NodeType nodeType) {
        this(id, name, nodeType);
        this.description = description;
    }

    // === Getters and Setters (Encapsulation) ===

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public NodeType getNodeType() { return nodeType; }
    public void setNodeType(NodeType nodeType) { this.nodeType = nodeType; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public List<WorkflowConnection> getOutgoingConnections() {
        return outgoingConnections;
    }

    public void addOutgoingConnection(WorkflowConnection connection) {
        this.outgoingConnections.add(connection);
    }

    public void removeConnection(WorkflowConnection connection) {
        this.outgoingConnections.remove(connection);
    }

    public void addConnection(WorkflowConnection connection) {
        this.outgoingConnections.add(connection);
    }

    // === Metadata Handling (Overloaded Methods) ===

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    public void addMetadata(String key) {
        this.metadata.put(key, "");
    }

    public void addMetadata(Map<String, String> allMetadata) {
        if (allMetadata != null) {
            this.metadata.putAll(allMetadata);
        }
    }

    // === Abstract Methods to be Implemented by Subclasses ===

    /**
     * Executes the logic associated with this node.
     * Must be overridden by concrete subclasses.
     * May throw {@link InvalidWorkflowException} if the node state is invalid.
     */
    public abstract void execute() throws InvalidWorkflowException;

    /**
     * Checks whether this node is valid for execution or connection.
     * Subclasses define their own criteria.
     *
     * @return true if the node is in a valid state
     * @throws InvalidWorkflowException if the validation fails
     */
    public abstract boolean isValid() throws InvalidWorkflowException;

    /**
     * Validates a given operation for this node type.
     * For example, START or END nodes may disallow certain actions.
     *
     * @param operation the operation string to validate
     * @throws UnsupportedOperationForNodeException if the operation is not allowed
     */
    public abstract void validateOperation(String operation) throws UnsupportedOperationForNodeException;

    /**
     * Wrapper method that throws exception if {@code isValid()} fails.
     *
     * @throws InvalidWorkflowException if validation fails
     */
    public void validateOrThrow() throws InvalidWorkflowException {
        if (!isValid()) {
            throw new InvalidWorkflowException("Node [" + name + "] is not valid.");
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (ID: %s)", nodeType, name, id);
    }

    // === Optional Utility ===

    public void setType(NodeType type) {
        this.nodeType = type;
    }

    public NodeType getType() {
        return nodeType;
    }
}
