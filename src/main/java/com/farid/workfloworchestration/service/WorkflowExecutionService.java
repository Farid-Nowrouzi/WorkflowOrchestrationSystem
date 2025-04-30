package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * WorkflowExecutionService
 *
 * Responsibilities:
 * - Execute a workflow starting from a node.
 * - Traverse through nodes via connections.
 *
 * OOP Concepts Applied:
 * - Encapsulation: Keeps execution logic inside.
 * - Abstraction: Simple executeWorkflow method hides details.
 * - Cohesion: Only execution, no validation, no UI.
 * - Information Hiding: Internal traversal hidden.
 * - Defensive Programming: Avoids cycles and nulls.
 */
public class WorkflowExecutionService {

    /**
     * Public entry point: Executes a workflow from the given node.
     *
     * @param startNode The starting node to execute.
     */
    public void executeWorkflow(WorkflowNode startNode) {
        if (startNode == null) {
            System.out.println("⚠️ Cannot execute a null node.");
            return;
        }

        Set<String> visited = new HashSet<>();
        executeWorkflowRecursive(startNode, visited, 0);
    }

    /**
     * Recursive helper with cycle detection and indentation for clarity.
     *
     * @param node     The current node to execute.
     * @param visited  Set of visited node IDs to prevent cycles.
     * @param depth    Current execution depth (for indentation).
     */
    private void executeWorkflowRecursive(WorkflowNode node, Set<String> visited, int depth) {
        if (node == null || visited.contains(node.getId())) {
            if (node != null) {
                log(depth, "🔁 Skipping already visited node: " + node.getName());
            }
            return;
        }

        visited.add(node.getId());
        log(depth, "✅ Executing node: " + node.getName() + " [" + node.getNodeType() + "]");
        node.execute();

        List<WorkflowConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null && !outgoingConnections.isEmpty()) {
            for (WorkflowConnection connection : outgoingConnections) {
                WorkflowNode target = connection.getTargetNode();
                log(depth + 1, "➡️ Following connection to: " + (target != null ? target.getName() : "null"));
                executeWorkflowRecursive(target, visited, depth + 1);
            }
        } else {
            log(depth + 1, "🛑 No further connections.");
        }
    }

    /**
     * Utility method for clean indented logs.
     *
     * @param depth The current depth (used for indentation).
     * @param message The message to log.
     */
    private void log(int depth, String message) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + message);
    }

    // 🔮 Optional: Future support for collecting results, logs, predictions, etc.
    // public void executeWorkflowWithCollector(WorkflowNode startNode, ResultCollector collector) { ... }
}
