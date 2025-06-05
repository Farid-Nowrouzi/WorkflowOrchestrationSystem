package com.farid.workfloworchestration.command;

import com.farid.workfloworchestration.controller.MainController;
import com.farid.workfloworchestration.controller.MainViewController;
import com.farid.workfloworchestration.model.WorkflowNode;

/**
 * Represents a command that connects two workflow nodes in the system.
 * This is part of the Command design pattern implementation, encapsulating
 * a single operation as an object.
 *
 * <p>Responsibilities include:</p>
 * <ul>
 *     <li>Creating a logical connection between nodes in the data model</li>
 *     <li>Triggering a visual connection (arrow) on the UI canvas</li>
 * </ul>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class ConnectNodesCommand implements WorkflowCommand { // Subtyping Polymorphism (via interface)

    // Encapsulation: these fields are private and final, hidden from external modification
    private final WorkflowNode source;              // Represents the origin node
    private final WorkflowNode target;              // Represents the destination node
    private final MainController mainController;    // Composition: this class depends on a controller to handle logic
    private final MainViewController controller;    // Composition: another controller to handle view updates

    /**
     * Constructor that injects all required dependencies for this command.
     * Demonstrates Dependency Injection by passing collaborators via constructor.
     *
     * @param source The source node for the connection
     * @param target The target node for the connection
     * @param mainController The logic controller that manages workflow operations
     * @param controller The visual controller that manages UI canvas updates
     */
    public ConnectNodesCommand(WorkflowNode source,
                               WorkflowNode target,
                               MainController mainController,
                               MainViewController controller) {
        this.source = source;                 // Encapsulation
        this.target = target;                 // Encapsulation
        this.mainController = mainController; // Composition, Dependency Injection
        this.controller = controller;         // Composition, Dependency Injection

        // This class no longer creates the connection object directly.
        // Instead, it delegates responsibility to the controller.
        // => This respects Abstraction, Cohesion, and Separation of Concerns.
    }

    /**
     * Executes the node connection operation. Delegates the logic to MainController.
     * Also supports clean separation between model logic and UI rendering.
     */
    @Override
    public void execute() {
        // Abstraction: Delegates the logic of connecting to mainController
        // Modularity: This method performs exactly one high-level task
        // Low Coupling: This class doesn't manage connection internals, reducing dependency on implementation
        // Reusability: The same command can be reused in different workflow contexts (e.g., redo, macro scripts)

        mainController.connectNodes(source, target, null);
    }
}
