package com.farid.workfloworchestration.controller;

import com.farid.workfloworchestration.exception.InvalidWorkflowException;
import com.farid.workfloworchestration.model.*;
import com.farid.workfloworchestration.factory.NodeFactory;
import com.farid.workfloworchestration.observer.WorkflowEventNotifier;
import com.farid.workfloworchestration.service.WorkflowService;
import com.farid.workfloworchestration.service.WorkflowValidationService;

import java.util.*;

/**
 * MainController manages all high-level workflow logic and node orchestration.
 *
 * OOP Principles Applied:
 * - Encapsulation: Contains all workflow control logic in one place.
 * - Abstraction: Exposes only high-level methods to the view (e.g., create, execute, connect).
 * - Dependency Injection: Accepts service and notifier as dependencies in constructor.
 * - High Cohesion: Handles only orchestration logic, not low-level node operations.
 * - Loose Coupling: Depends on interfaces like WorkflowService, not concrete classes.
 */
public class MainController {

    //  Reviewed: Fields are private (info hiding respected); controller logic scoped correctly.
//  Verified encapsulation and abstraction are properly applied in MainController.


    // Services responsible for managing workflow logic and broadcasting events
    private final WorkflowService workflowService;
    private final WorkflowEventNotifier eventNotifier;

    // Reference to the JavaFX UI controller for visual interaction
    private MainViewController mainViewController;

    // Constructor with dependency injection
    public MainController(WorkflowService workflowService, WorkflowEventNotifier eventNotifier) {
        this.workflowService = workflowService;
        this.eventNotifier = eventNotifier;
    }

    /**
     * Setter for injecting the JavaFX MainViewController into the backend controller.
     */
    public void setMainViewController(MainViewController viewController) {
        this.mainViewController = viewController;
    }

    public WorkflowService getWorkflowService() {
        return workflowService;
    }

    /**
     * Creates and registers a new node using the NodeFactory.
     *
     * @param nodeType The type of the node (e.g., TASK, CONDITION).
     * @param id       The unique ID of the node.
     * @param name     The human-readable name of the node.
     * @return The newly created node.
     */
    public WorkflowNode createNode(NodeType nodeType, String id, String name) {
        WorkflowNode node = NodeFactory.createNode(nodeType, id, name);
        workflowService.addNode(node); // Persist node in service
        eventNotifier.notifyObservers(node, "Node Created"); // Notify UI/observers
        return node;
    }

    /**
     * Connects two workflow nodes with an optional condition (used for branching).
     *
     * @param source    Source node.
     * @param target    Target node.
     * @param condition Label like "YES" or "NO" for ConditionNode logic.
     */
    public void connectNodes(WorkflowNode source, WorkflowNode target, String condition) {
        WorkflowConnection connection = new WorkflowConnection(source, target, condition);
        workflowService.addConnection(connection);

        // Extra logic for CONDITION nodes — set yes/no targets
        if (source instanceof ConditionNode conditionNode) {
            if ("YES".equalsIgnoreCase(condition)) {
                conditionNode.setYesTarget(target);
            } else if ("NO".equalsIgnoreCase(condition)) {
                conditionNode.setNoTarget(target);
            }
        }

        eventNotifier.notifyObservers(source, "Connected to " + target.getName());

        // Visualize the connection on the canvas via UI
        if (mainViewController != null) {
            mainViewController.connectNodes(source, target, connection.getCondition());
        }
    }

    /**
     * Removes a connection between two nodes.
     */
    public void disconnectNodes(WorkflowNode source, WorkflowNode target) {
        workflowService.removeConnection(source, target);
    }

    /**
     * Executes the entire workflow starting from all detected START nodes.
     * Includes validation before execution.
     */
    public void executeWorkflow() {
        List<WorkflowNode> startNodes = workflowService.findStartNodes();
        List<WorkflowNode> allNodes = workflowService.getAllNodes();

        if (startNodes.isEmpty()) {
            System.err.println(" No start nodes found. Workflow cannot be executed.");
            return;
        }

        // Validate structure before executing
        WorkflowValidationService validator = new WorkflowValidationService();
        for (WorkflowNode startNode : startNodes) {
            boolean valid = validator.validateWorkflow(startNode, allNodes);
            if (!valid) {
                System.err.println(" Workflow validation failed. Execution stopped.");
                return;
            }
        }

        visitedNodes.clear(); // Reset visited tracking for DFS traversal

        // Recursively execute each workflow from all valid start nodes
        for (WorkflowNode startNode : startNodes) {
            executeNode(startNode);
        }
    }

    /**
     * Clears the entire workflow — all nodes and connections.
     */
    public void clearWorkflow() {
        if (workflowService != null) {
            workflowService.clearAll();
            System.out.println("MainController: Workflow has been cleared.");
        } else {
            System.err.println("MainController: WorkflowService is null, cannot clear workflow.");
        }
    }

    // Keeps track of already executed nodes (avoid infinite loops)
    private final Set<String> visitedNodes = new HashSet<>();

    /**
     * Recursively executes a node and its connected children.
     * Handles passive nodes, conditional branching, and normal executable nodes.
     *
     * @param node The node to execute.
     */
    private void executeNode(WorkflowNode node) {
        if (visitedNodes.contains(node.getId())) return; // Avoid re-executing
        visitedNodes.add(node.getId());

        NodeType type = node.getType();

        // Skip passive nodes (e.g., DATA, OUTPUT, START, END)
        if (type == NodeType.OUTPUT || type == NodeType.DATA || type == NodeType.START || type == NodeType.END) {
            System.out.println(" Skipped passive node: " + node.getName() + " (Type: " + type + ")");
            return;
        }

        // Special case for branching node (ConditionNode)
        if (node instanceof ConditionNode conditionNode) {
            Map<String, String> dummyContext = new HashMap<>();
            if (mainViewController != null) {
                mainViewController.evaluateConditionNode(conditionNode, dummyContext); // UI determines path
            } else {
                System.err.println(" ConditionNode evaluation skipped: mainViewController not set.");
            }
            return;
        }

        // Executable node (e.g., TRAINING, INFERENCE, etc.)
        if (node instanceof ExecutableNode) {
            System.out.println(" Executing node: " + node.getName());
            try {
                node.execute();
            } catch (InvalidWorkflowException e) {
                System.err.println(" Execution failed for node '" + node.getName() + "': " + e.getMessage());
            }
        } else {
            System.out.println(" Unknown node type: " + node.getName());
        }

        // Recursively continue execution for connected nodes
        List<WorkflowConnection> connections = workflowService.getConnectionsFrom(node);
        for (WorkflowConnection conn : connections) {
            executeNode(conn.getTargetNode());
        }
    }
}
