package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.WorkflowNode;
import com.farid.workfloworchestration.model.WorkflowConnection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WorkflowValidationService
 *
 * Responsibilities:
 * - Validate the structure of the workflow.
 * - Check for unreachable nodes, cycles, etc.
 *
 * OOP Concepts Applied:
 * - Encapsulation: Keeps validation rules hidden.
 * - Abstraction: validateWorkflow method provides simple access.
 * - Cohesion: Only validation, no execution.
 * - Information Hiding: Internally uses helper methods.
 */
public class WorkflowValidationService {

    /**
     * Validates that the workflow has no unreachable nodes or cycles.
     *
     * @param startNode The starting node of the workflow.
     * @param allNodes  Optional: full list of nodes (to detect unreachable ones).
     * @return true if valid, false otherwise.
     */
    public boolean validateWorkflow(WorkflowNode startNode, List<WorkflowNode> allNodes) {
        if (startNode == null) {
            System.out.println("❌ Validation failed: Start node is null.");
            return false;
        }

        Set<WorkflowNode> visited = new HashSet<>();
        Set<WorkflowNode> recursionStack = new HashSet<>();

        boolean hasCycle = detectCycle(startNode, visited, recursionStack);
        if (hasCycle) {
            System.out.println("❌ Validation failed: Cycle detected in workflow.");
            return false;
        }

        // Check reachability if full list provided
        if (allNodes != null && !allNodes.isEmpty()) {
            Set<WorkflowNode> reachable = new HashSet<>();
            traverseWorkflow(startNode, reachable);

            for (WorkflowNode node : allNodes) {
                if (!reachable.contains(node)) {
                    System.out.println("⚠️ Unreachable node: " + node.getName() + " (" + node.getId() + ")");
                }
            }

            System.out.println("✅ Reachable nodes: " + reachable.size() + "/" + allNodes.size());
        }

        System.out.println("✅ Workflow validation passed.");
        return true;
    }

    /**
     * Detects cycles in the workflow using DFS.
     */
    private boolean detectCycle(WorkflowNode node, Set<WorkflowNode> visited, Set<WorkflowNode> recursionStack) {
        if (recursionStack.contains(node)) {
            return true; // Cycle detected
        }
        if (visited.contains(node)) {
            return false; // Already checked
        }

        visited.add(node);
        recursionStack.add(node);

        List<WorkflowConnection> connections = node.getOutgoingConnections();
        if (connections != null) {
            for (WorkflowConnection conn : connections) {
                if (detectCycle(conn.getTargetNode(), visited, recursionStack)) {
                    return true;
                }
            }
        }

        recursionStack.remove(node);
        return false;
    }

    /**
     * Traverses the workflow and collects all reachable nodes.
     */
    private void traverseWorkflow(WorkflowNode node, Set<WorkflowNode> visited) {
        if (node == null || visited.contains(node)) return;

        visited.add(node);
        List<WorkflowConnection> connections = node.getOutgoingConnections();
        if (connections != null) {
            for (WorkflowConnection conn : connections) {
                traverseWorkflow(conn.getTargetNode(), visited);
            }
        }
    }
}
