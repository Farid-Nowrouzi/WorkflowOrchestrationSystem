package com.farid.workfloworchestration.command;

import com.farid.workfloworchestration.controller.MainViewController;
import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.WorkflowNode;

/**
 * Command class responsible for creating a new node in the workflow.
 * This includes adding the node visually to the canvas and optionally
 * connecting it to an existing source node.
 *
 * <p>This class is part of the Command design pattern, encapsulating
 * all logic needed to perform the creation as a reusable and undoable action.</p>
 *
 * <p>By separating this logic, we maintain clean modularity, abstraction,
 * and support features like undo/redo.</p>
 *
 * @author Your Name
 * @version 1.0
 */
public class CreateNodeCommand implements WorkflowCommand { // Subtyping Polymorphism (via interface)

    // Encapsulation: All fields are private and immutable
//  All fields follow information hiding (private + final). No external access or exposure needed.

    private final WorkflowNode node;                     // The new node to be added
    private final MainViewController controller;         // Composition: delegates UI work to controller
    private final WorkflowNode sourceNode;               // The node from which the connection starts
    private final WorkflowConnection connection;         // Optional: connection from source to new node

    /**
     * Constructs a CreateNodeCommand by injecting all required dependencies.
     * Demonstrates both Composition and Dependency Injection.
     *
     * @param node The new node to add to the canvas and model
     * @param sourceNode The existing node to link from (can be null)
     * @param connection The connection object linking nodes (optional)
     * @param controller Responsible for rendering UI elements
     */
    public CreateNodeCommand(WorkflowNode node,
                             WorkflowNode sourceNode,
                             WorkflowConnection connection,
                             MainViewController controller) {
        this.node = node;                 // Encapsulation
        this.sourceNode = sourceNode;     // Encapsulation
        this.connection = connection;     // Encapsulation
        this.controller = controller;     // Composition, Dependency Injection
    }

    /**
     * Executes the command to:
     * 1. Add the node visually to the UI canvas
     * 2. Connect it to a source node, if applicable
     */
    @Override
    public void execute() {
        // Abstraction: Delegate rendering logic to the UI controller
        controller.addNodeToCanvas(node);

        // Composition: A node has a list of outgoing connections (has-a relationship)
        // Checks if this is a linked creation, and updates the model structure
        if (sourceNode != null && connection != null) {
            sourceNode.addOutgoingConnection(connection);
        }

        // Modularity: Each command has a single purpose, encapsulated here
    }
}
