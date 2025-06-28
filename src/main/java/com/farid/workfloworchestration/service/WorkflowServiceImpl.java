package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.NodeType;
import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.ArrayList;
import java.util.List;

/**
 * WorkflowServiceImpl
 *
 * <p>This class provides a concrete implementation of the {@link WorkflowService} interface.
 * It manages the creation, connection, removal, and querying of workflow nodes.</p>
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Abstraction:</b> Implements a well-defined service interface.</li>
 *   <li><b>Encapsulation:</b> Keeps internal node and connection lists private.</li>
 *   <li><b>Cohesion:</b> Focuses only on structural workflow logic (not execution or UI).</li>
 *   <li><b>Loose Coupling:</b> Can be replaced or mocked without affecting consumers.</li>
 * </ul>
 */
public class WorkflowServiceImpl implements WorkflowService {

    // === Private Fields (Information Hiding Compliance) ===
// These fields store internal state and are only accessible via public interface methods.
// Ensures full encapsulation and avoids unintended external access.


    // === Internal State ===

    private final List<WorkflowNode> nodes = new ArrayList<>();
    private final List<WorkflowConnection> connections = new ArrayList<>();


    // === Node Management ===

    @Override
    public void addNode(WorkflowNode node) {
        if (node == null) {
            System.out.println(" Cannot add null node.");
            return;
        }
        nodes.add(node);
    }


    @Override
    public void removeNode(WorkflowNode node) {
        if (node == null) return;

        // Remove all incoming/outgoing connections associated with the node
        connections.removeIf(connection ->
                connection.getSourceNode().equals(node) || connection.getTargetNode().equals(node)
        );

        nodes.remove(node);
        System.out.println(" Removed node: " + node.getName());
    }

    @Override
    public List<WorkflowNode> getAllNodes() {
        return nodes;
    }

    @Override
    public WorkflowNode findNodeById(String id) {
        for (WorkflowNode node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public List<WorkflowNode> findStartNodes() {
        return nodes.stream()
                .filter(n -> n.getNodeType() == NodeType.START)
                .toList();
    }


    // === Connection Management ===

    @Override
    public void addConnection(WorkflowConnection connection) {
        if (connection == null || connection.getSourceNode() == null) {
            System.out.println(" Invalid connection or source node.");
            return;
        }

        try {
            connections.add(connection); // Global list
            connection.getSourceNode().addConnection(connection); // May throw
        } catch (Exception e) {
            System.out.println(" Failed to add connection: " + e.getMessage());
        }
    }


    @Override
    public void removeConnection(WorkflowNode source, WorkflowNode target) {
        if (source == null || target == null) {
            System.out.println(" Source or target is null.");
            return;
        }

        try {
            WorkflowConnection toRemove = null;
            for (WorkflowConnection connection : connections) {
                if (connection.getSourceNode().equals(source) &&
                        connection.getTargetNode().equals(target)) {
                    toRemove = connection;
                    break;
                }
            }

            if (toRemove != null) {
                connections.remove(toRemove);
                System.out.println(" Connection removed: " + source.getName() + " → " + target.getName());
            } else {
                System.out.println(" No connection found between: " + source.getName() + " and " + target.getName());
            }
        } catch (Exception e) {
            System.out.println(" Error removing connection: " + e.getMessage());
        }
    }


    @Override
    public List<WorkflowConnection> getAllConnections() {
        return connections;
    }

    @Override
    public List<WorkflowConnection> getConnectionsFrom(WorkflowNode node) {
        List<WorkflowConnection> outgoing = new ArrayList<>();
        for (WorkflowConnection connection : connections) {
            if (connection.getSourceNode().equals(node)) {
                outgoing.add(connection);
            }
        }
        return outgoing;
    }

    @Override
    public List<WorkflowConnection> getConnectionsTo(WorkflowNode node) {
        List<WorkflowConnection> incoming = new ArrayList<>();
        for (WorkflowConnection connection : connections) {
            if (connection.getTargetNode().equals(node)) {
                incoming.add(connection);
            }
        }
        return incoming;
    }


    // === Global Reset ===

    @Override
    public void clearAll() {
        nodes.clear();
        connections.clear();
        System.out.println(" Cleared all workflow nodes and connections.");
    }
}
