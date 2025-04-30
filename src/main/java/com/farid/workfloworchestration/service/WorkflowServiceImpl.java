package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete implementation of WorkflowService.
 * Handles workflow node and connection management.
 */
public class WorkflowServiceImpl implements WorkflowService {

    private final List<WorkflowNode> nodes = new ArrayList<>();
    private final List<WorkflowConnection> connections = new ArrayList<>();

    @Override
    public void addNode(WorkflowNode node) {
        nodes.add(node);
    }

    @Override
    public void addConnection(WorkflowConnection connection) {
        connections.add(connection);
    }

    @Override
    public List<WorkflowNode> findStartNodes() {
        // Simple placeholder: return all nodes for now
        return nodes;
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
    public List<WorkflowNode> getAllNodes() {
        return nodes;
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
    public List<WorkflowConnection> getAllConnections() {
        return connections;
    }



    // 🌟 NEW: Clear all nodes and connections
    @Override
    public void clearAll() {
        nodes.clear();
        connections.clear();
        System.out.println("WorkflowService: Cleared all nodes and connections.");

    }
}

