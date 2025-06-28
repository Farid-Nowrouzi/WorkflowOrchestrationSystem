package com.farid.workfloworchestration.model;

import java.util.List;

/**
 * UndoableAction
 *
 * Represents an undoable or redoable user action in the workflow editor.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Encapsulation:</b> Fields are private with controlled access through getters/setters.</li>
 *   <li><b>Constructor Overloading:</b> Different constructors handle node actions and connection actions.</li>
 *   <li><b>Single Responsibility Principle (SRP):</b> Encapsulates the details of a single user action.</li>
 *   <li><b>Composition:</b> Composes complex actions using references to {@code WorkflowNode} and {@code WorkflowConnection}.</li>
 * </ul>
 */
public class UndoableAction {

    /**
     * Enum to classify the type of undoable action.
     */
    public enum ActionType {
        CREATE_NODE,
        DELETE_NODE,
        MOVE_NODE,
        CONNECT_NODES,
        DISCONNECT_NODES
    }

    // === Private Fields (Information Hiding Compliance) ===

    // Immutable action classification
    private final ActionType actionType;

    // Immutable node-related coordinates and references
    private final WorkflowNode node;
    private final double oldX;
    private final double oldY;
    private final double newX;
    private final double newY;

    // Immutable source/target reference for connections
    private final WorkflowNode sourceNode;
    private final WorkflowNode targetNode;

    // Mutable fields (can change after construction via setters)
    private List<WorkflowConnection> savedConnections;
    private String connectionLabel;

    // === Constructor Overloading ===

    /**
     * Constructor for MOVE_NODE actions with coordinate tracking.
     */
    public UndoableAction(ActionType actionType, WorkflowNode node, double oldX, double oldY, double newX, double newY) {
        this.actionType = actionType;
        this.node = node;
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.sourceNode = null;
        this.targetNode = null;
    }

    /**
     * Constructor for CONNECT_NODES or DISCONNECT_NODES actions.
     */
    public UndoableAction(ActionType actionType, WorkflowNode sourceNode, WorkflowNode targetNode) {
        this.actionType = actionType;
        this.node = null;
        this.oldX = 0;
        this.oldY = 0;
        this.newX = 0;
        this.newY = 0;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    /**
     * Constructor for DELETE_NODE actions that also store removed connections.
     */
    public UndoableAction(ActionType actionType, WorkflowNode node, List<WorkflowConnection> savedConnections) {
        this.actionType = actionType;
        this.node = node;
        this.oldX = 0;
        this.oldY = 0;
        this.newX = 0;
        this.newY = 0;
        this.sourceNode = null;
        this.targetNode = null;
        this.savedConnections = savedConnections;
    }

    /**
     * Checks if this action involves a given node.
     */
    public boolean involvesNode(WorkflowNode n) {
        return (node != null && node.equals(n)) ||
                (sourceNode != null && sourceNode.equals(n)) ||
                (targetNode != null && targetNode.equals(n));
    }

    // === Getters (Encapsulation) ===

    public ActionType getActionType() {
        return actionType;
    }

    public WorkflowNode getNode() {
        return node;
    }

    public double getOldX() {
        return oldX;
    }

    public double getOldY() {
        return oldY;
    }

    public double getNewX() {
        return newX;
    }

    public double getNewY() {
        return newY;
    }

    public WorkflowNode getSourceNode() {
        return sourceNode;
    }

    public WorkflowNode getTargetNode() {
        return targetNode;
    }

    public List<WorkflowConnection> getSavedConnections() {
        return savedConnections;
    }

    public String getConnectionLabel() {
        return connectionLabel;
    }

    // === Setters (Controlled Write Access for Mutables) ===

    public void setSavedConnections(List<WorkflowConnection> savedConnections) {
        this.savedConnections = savedConnections;
    }

    public void setConnectionLabel(String label) {
        this.connectionLabel = label;
    }
}
