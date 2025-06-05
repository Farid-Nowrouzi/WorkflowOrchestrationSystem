package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * WorkflowValidationService
 *
 * <p>This service class is responsible for verifying the structural correctness of a workflow
 * before execution. It enforces constraints such as absence of cycles, node reachability,
 * and logical correctness based on node types.</p>
 *
 * <p><strong>OOP Design Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Encapsulation:</b> Validation logic is fully contained within this class.</li>
 *   <li><b>Abstraction:</b> Consumers use a single public method {@code validateWorkflow()}.</li>
 *   <li><b>Cohesion:</b> The class is dedicated to validation—no execution or persistence logic.</li>
 *   <li><b>Information Hiding:</b> Internal mechanisms like cycle detection and traversal are private.</li>
 * </ul>
 */
public class WorkflowValidationService {

    /**
     * Validates a workflow graph.
     *
     * @param startNode The designated entry point of the workflow.
     * @param allNodes  The full list of nodes in the workflow (used for reachability checks).
     * @return {@code true} if the workflow is valid; {@code false} otherwise.
     */
    public boolean validateWorkflow(WorkflowNode startNode, List<WorkflowNode> allNodes) {
        if (startNode == null) {
            System.out.println("❌ Validation failed: Start node is null.");
            return false;
        }

        // Step 1: Detect cycles using DFS and recursion stack
        Set<WorkflowNode> visited = new HashSet<>();
        Set<WorkflowNode> recursionStack = new HashSet<>();
        if (detectCycle(startNode, visited, recursionStack)) {
            System.out.println("❌ Validation failed: Cycle detected in workflow.");
            return false;
        }

        // Step 2: Check reachability of all nodes (from start node)
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

        // Step 3: Warn about logical errors in node structure
        for (WorkflowNode node : allNodes) {
            int outgoing = (node.getOutgoingConnections() != null)
                    ? node.getOutgoingConnections().size()
                    : 0;

            // TASK nodes should only have a single outgoing path
            if ("TASK".equalsIgnoreCase(node.getNodeType().toString()) && outgoing > 1) {
                System.out.println("⚠️ Task node '" + node.getName() +
                        "' has " + outgoing + " outgoing connections. Consider splitting the logic.");
            }
        }

        System.out.println("✅ Workflow validation passed.");
        return true;
    }

    /**
     * Recursive DFS method to detect cycles in the graph.
     *
     * @param node           Current node being explored.
     * @param visited        Set of permanently visited nodes.
     * @param recursionStack Set of nodes in the current DFS path (to detect back edges).
     * @return {@code true} if a cycle is found.
     */
    private boolean detectCycle(WorkflowNode node, Set<WorkflowNode> visited, Set<WorkflowNode> recursionStack) {
        if (recursionStack.contains(node)) return true; // Cycle detected
        if (visited.contains(node)) return false;       // Already explored

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

        recursionStack.remove(node); // Backtrack
        return false;
    }

    /**
     * Recursively traverses the workflow graph from a given node to collect reachable nodes.
     *
     * @param node    The current node being visited.
     * @param visited Set of nodes already marked as reachable.
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
