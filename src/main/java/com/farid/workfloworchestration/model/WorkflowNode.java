package com.farid.workfloworchestration.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

// Demonstrates: Encapsulation, Abstraction, Inheritance, Modularity, Code Reuse
public abstract class WorkflowNode {
    // Encapsulation: Private fields
    private String id;
    private String name;
    private String description;
    private NodeType nodeType;
    private Map<String, String> metadata; // For dynamic info (flexible)
    private LocalDateTime createdAt; // Timestamp
    // Inside WorkflowNode.java
    public abstract boolean isValid();


    // New: Outgoing connections (Association)
    private List<WorkflowConnection> outgoingConnections;
    private String colorHex = "#ffffff"; // default whit

    // Constructor
    public WorkflowNode(String id, String name, NodeType nodeType) {
        this.id = id;
        this.name = name;
        this.nodeType = nodeType;
        this.metadata = new HashMap<>();
        this.createdAt = LocalDateTime.now();
        this.outgoingConnections = new ArrayList<>(); // Initialize empty list
    }

    // Overloaded Constructor (with description)
    public WorkflowNode(String id, String name, String description, NodeType nodeType) {
        this(id, name, nodeType);
        this.description = description;
    }

    // Getters and Setters (Encapsulation)
    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public NodeType getNodeType() { return nodeType; }

    public void setNodeType(NodeType nodeType) { this.nodeType = nodeType; }

    public Map<String, String> getMetadata() { return metadata; }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }



    public LocalDateTime getCreatedAt() { return createdAt; }

    // New: Manage outgoing connections
    public List<WorkflowConnection> getOutgoingConnections() {
        return outgoingConnections;
    }

    public void addOutgoingConnection(WorkflowConnection connection) {
        this.outgoingConnections.add(connection);
    }

    // Abstraction: Different nodes will implement this differently (Polymorphism)
    public abstract void execute();

    @Override
    public String toString() {
        return String.format("[%s] %s (ID: %s)", nodeType, name, id);
    }

}
