package com.farid.workfloworchestration.model;

import java.util.List;

/**
 * Represents an undoable or redoable action in the workflow editor.
 *
 * It supports actions on nodes (create, delete, move) and connections (connect, disconnect).
 */
public class UndoableAction {

    public enum ActionType {
        CREATE_NODE,
        DELETE_NODE,
        MOVE_NODE,
        CONNECT_NODES,
        DISCONNECT_NODES
    }

    private final ActionType actionType;

    // For node-related actions
    private final WorkflowNode node;
    private final double oldX;
    private final double oldY;
    private final double newX;
    private final double newY;

    // For connection-related actions
    private final WorkflowNode sourceNode;
    private final WorkflowNode targetNode;

    // For DELETE_NODE actions (undo restore)
    private List<WorkflowConnection> savedConnections;

    // === Constructors ===

    // For MOVE_NODE actions (move with position tracking)
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

    public boolean involvesNode(WorkflowNode n) {
        return (node != null && node.equals(n)) ||
                (sourceNode != null && sourceNode.equals(n)) ||
                (targetNode != null && targetNode.equals(n));
    }


    // For CONNECT_NODES / DISCONNECT_NODES actions
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

    // 🌟 NEW: For DELETE_NODE with saved connections
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

    // === Getters ===

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

    // 🌟 Get/Set for DELETE_NODE saved connections
    public List<WorkflowConnection> getSavedConnections() {
        return savedConnections;
    }

    public void setSavedConnections(List<WorkflowConnection> savedConnections) {
        this.savedConnections = savedConnections;
    }
}
