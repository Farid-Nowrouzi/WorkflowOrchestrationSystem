package com.farid.workfloworchestration.factory;

import com.farid.workfloworchestration.model.*;

/**
 * NodeFactory is responsible for creating different types of WorkflowNodes.
 *
 * OOP Concepts Applied:
 * - Factory Design Pattern
 * - Encapsulation & Abstraction
 * - Ad-hoc Polymorphism (Overloading)
 * - Loose Coupling
 * - Optional: Early validation after creation
 */
public class NodeFactory {

    /**
     * Creates a node with just ID and name (basic usage).
     */
    public static WorkflowNode createNode(NodeType nodeType, String id, String name) {
        switch (nodeType) {
            case TASK:
                return new TaskNode(id, name);
            case CONDITION:
                return new ConditionNode(id, name);
            case PREDICTION:
                return new PredictionNode(id, name);
            default:
                throw new IllegalArgumentException("Invalid node type: " + nodeType);
        }
    }

    /**
     * Overloaded: Create a node with extraInfo (like conditionExpression or taskDetails).
     */
    public static WorkflowNode createNode(NodeType nodeType, String id, String name, String extraInfo) {
        WorkflowNode node;
        switch (nodeType) {
            case TASK:
                node = new TaskNode(id, name, extraInfo);
                break;
            case CONDITION:
                node = new ConditionNode(id, name, "Generated condition", extraInfo);
                break;
            case PREDICTION:
                node = new PredictionNode(id, name, "Generated prediction node", extraInfo);
                break;
            default:
                throw new IllegalArgumentException("Invalid node type: " + nodeType);
        }

        // Optional: Validate after creation (based on isValid() if implemented)
        if (!node.isValid()) {
            throw new IllegalStateException("Created node is invalid: " + node.getName());
        }

        return node;
    }

    /**
     * Overloaded: Create a node with auto-generated name and ID.
     */
    public static WorkflowNode createNode(NodeType nodeType) {
        return createNode(nodeType, "AUTO_ID_" + System.currentTimeMillis(), "AUTO_NAME");
    }

    /**
     * Overloaded: Create a node with only ID and default name.
     */
    public static WorkflowNode createNode(NodeType nodeType, String id) {
        return createNode(nodeType, id, "Unnamed Node");
    }
}
