package com.farid.workfloworchestration.controller;

import com.farid.workfloworchestration.model.WorkflowNode;
import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.factory.NodeFactory;
import com.farid.workfloworchestration.model.NodeType;
import com.farid.workfloworchestration.observer.WorkflowEventNotifier;
import com.farid.workfloworchestration.service.WorkflowService;

import java.util.List;

/**
 * MainController class.
 *
 * OOP Concepts Applied:
 * - Encapsulation: Controls access to workflow management
 * - Abstraction: Hides lower details (only exposes user-level methods)
 * - Dependency Injection: Service and Notifier injected into Controller
 * - High Cohesion: Focused only on controlling workflow logic
 * - Loose Coupling: Works with interfaces, not direct implementations
 */
public class MainController {

    private final WorkflowService workflowService;
    private final WorkflowEventNotifier eventNotifier;


    public MainController(WorkflowService workflowService, WorkflowEventNotifier eventNotifier) {
        this.workflowService = workflowService;
        this.eventNotifier = eventNotifier;
    }

    public WorkflowService getWorkflowService() {
        return workflowService;
    }


    // Create and add a new node
    public WorkflowNode createNode(NodeType nodeType, String id, String name) {
        WorkflowNode node = NodeFactory.createNode(nodeType, id, name);
        workflowService.addNode(node);
        eventNotifier.notifyObservers(node, "Node Created");
        return node;
    }

    // Connect two nodes
    public void connectNodes(WorkflowNode source, WorkflowNode target, String condition) {
        WorkflowConnection connection = new WorkflowConnection(source, target, condition);
        workflowService.addConnection(connection);
        eventNotifier.notifyObservers(source, "Connected to " + target.getName());
    }

    // Execute the workflow
    public void executeWorkflow() {
        List<WorkflowNode> startNodes = workflowService.findStartNodes();
        for (WorkflowNode node : startNodes) {
            executeNode(node);
        }
    }

    /**
     * Clears all nodes and connections from the workflow.
     * This delegates the clearing to the WorkflowService.
     */
    public void clearWorkflow() {
        if (workflowService != null) {
            workflowService.clearAll();
            System.out.println("MainController: Workflow has been cleared.");
        } else {
            System.err.println("MainController: WorkflowService is null, cannot clear workflow.");
        }
    }


    // Internal helper to execute a node and follow connections
    private void executeNode(WorkflowNode node) {
        node.execute();
        List<WorkflowConnection> connections = workflowService.getConnectionsFrom(node);
        for (WorkflowConnection connection : connections) {
            executeNode(connection.getTargetNode());
        }
    }
}

