package com.farid.workfloworchestration.service;

import com.farid.workfloworchestration.exception.InvalidWorkflowException;
import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * Handles the execution of a workflow graph by traversing connected nodes.
 *
 * <p>This class focuses solely on executing the logical flow between nodes
 * without managing validation, UI updates, or data collection.</p>
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 *   <li>Traverse the directed graph starting from a specified node.</li>
 *   <li>Execute each node while preventing infinite loops (cycle detection).</li>
 *   <li>Log execution steps for debugging or demonstration purposes.</li>
 * </ul>
 *
 * <p><strong>OOP Principles Applied:</strong></p>
 * <ul>
 *   <li><b>Encapsulation:</b> Execution logic is private and self-contained.</li>
 *   <li><b>Abstraction:</b> Client code only calls <code>executeWorkflow()</code>.</li>
 *   <li><b>Cohesion:</b> Class is focused on one responsibility—execution.</li>
 *   <li><b>Information Hiding:</b> Internal state and logging format are hidden.</li>
 *   <li><b>Defensive Programming:</b> Protects against nulls and infinite cycles.</li>
 * </ul>
 */
public class WorkflowExecutionService {

    // No instance fields present.
// Class adheres to information hiding by containing only method-local variables.
// Logic is accessed through clean public API (executeWorkflow), with helper methods kept private.


    /**
     * Executes the workflow starting from the specified node.
     *
     * @param startNode the node to begin execution from
     */
    public void executeWorkflow(WorkflowNode startNode) {
        if (startNode == null) {
            System.out.println(" Cannot execute a null node.");
            return;
        }

        Set<String> visited = new HashSet<>();
        executeWorkflowRecursive(startNode, visited, 0);
    }

    /**
     * Internal recursive method for depth-first execution.
     *
     * @param node    the current node being processed
     * @param visited set of node IDs to detect and avoid cycles
     * @param depth   the current recursion level (used for indentation)
     */
    private void executeWorkflowRecursive(WorkflowNode node, Set<String> visited, int depth) {
        if (node == null || visited.contains(node.getId())) {
            if (node != null) {
                log(depth, " Skipping already visited node: " + node.getName());
            }
            return;
        }

        visited.add(node.getId());
        log(depth, " Executing node: " + node.getName() + " [" + node.getNodeType() + "]");

        try {
            node.execute();
        } catch (InvalidWorkflowException e) {
            log(depth, " Execution failed for node '" + node.getName() + "': " + e.getMessage());
            return;
        } catch (Exception e) {
            log(depth, " Unexpected error while executing node '" + node.getName() + "': " + e.getMessage());
            return;
        }


        List<WorkflowConnection> outgoingConnections = node.getOutgoingConnections();
        if (outgoingConnections != null && !outgoingConnections.isEmpty()) {
            for (WorkflowConnection connection : outgoingConnections) {
                WorkflowNode target = connection.getTargetNode();
                log(depth + 1, " Following connection to: " + (target != null ? target.getName() : "null"));
                executeWorkflowRecursive(target, visited, depth + 1);
            }
        } else {
            log(depth + 1, " No further connections.");
        }
    }

    /**
     * Logs a message with indentation based on recursion depth.
     *
     * @param depth   number of levels deep in the workflow
     * @param message the message to print
     */
    private void log(int depth, String message) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + message);
    }

    // Future Extension: Execute with a result collector, logging system, or callback
    // public void executeWorkflowWithCollector(WorkflowNode startNode, ResultCollector collector) { ... }
}
