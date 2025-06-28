package com.farid.workfloworchestration.command;

import com.farid.workfloworchestration.model.WorkflowNode;
import com.farid.workfloworchestration.controller.MainViewController;
import com.farid.workfloworchestration.model.WorkflowConnection;
import com.farid.workfloworchestration.model.UndoableAction;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import com.farid.workfloworchestration.controller.MainController;
import com.farid.workfloworchestration.view.Arrow;

import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Command that deletes a node from the workflow.
 *
 * <p>This class encapsulates the complete deletion logic:
 * <ul>
 *     <li>Removes the node from the UI canvas</li>
 *     <li>Removes its associated arrows and mappings</li>
 *     <li>Saves the deletion as an undoable action</li>
 * </ul>
 *
 * It is part of the Command pattern structure and supports undo/redo logic.</p>
 *
 * @author Farid Noworuzi
 * @version 1.0
 */
// All attributes in this class are private and final — verified for information hiding as per professor’s instruction.

public class DeleteNodeCommand implements WorkflowCommand { // Subtyping Polymorphism

    // Encapsulation: All class fields are private and final to protect internal state
    private final WorkflowNode node;
    private final MainController mainController;               // Composition: this command has a controller
    private final MainViewController controller;               // Composition
    private final VBox nodeView;                               // UI representation of the node
    private final Map<String, Arrow> connectionArrows;         // Arrows associated with node connections
    private final Map<String, VBox> nodeViewsMap;              // Map from node IDs to UI components
    private final Pane workspacePane;                          // Main canvas UI pane
    private final Stack<UndoableAction> undoStack;             // Used to support undo operations

    /**
     * Constructs a DeleteNodeCommand with injected dependencies.
     * Demonstrates both Composition and Dependency Injection.
     *
     * @param node Node to delete
     * @param mainController Backend controller for model operations
     * @param controller Frontend controller for UI updates
     * @param nodeView UI component of the node
     * @param connectionArrows Map of arrows in the UI
     * @param nodeViewsMap Map of node ID to visual component
     * @param workspacePane The UI canvas
     * @param undoStack Stack to store undoable actions
     */
    public DeleteNodeCommand(
            WorkflowNode node,
            MainController mainController,
            MainViewController controller,
            VBox nodeView,
            Map<String, Arrow> connectionArrows,
            Map<String, VBox> nodeViewsMap,
            Pane workspacePane,
            Stack<UndoableAction> undoStack
    ) {
        this.node = node;                               // Encapsulation
        this.mainController = mainController;           // Composition, Dependency Injection
        this.controller = controller;                   // Composition, Dependency Injection
        this.nodeView = nodeView;
        this.connectionArrows = connectionArrows;
        this.nodeViewsMap = nodeViewsMap;
        this.workspacePane = workspacePane;
        this.undoStack = undoStack;
    }

    /**
     * Executes the node deletion process, including UI and model cleanup.
     * Pushes an undoable action onto the undo stack for reversal support.
     */
    @Override
    public void execute() {
        // Abstraction: Delegates connection fetching to workflowService
        List<WorkflowConnection> connectionsToSave = mainController.getWorkflowService().getAllConnections()
                .stream()
                .filter(conn -> conn.getSourceNode().equals(node) || conn.getTargetNode().equals(node))
                .toList();

        // Step 1: Remove the node from the visual workspace
        workspacePane.getChildren().remove(nodeView); // Low Coupling: operates via JavaFX public API

        // Step 2: Remove all arrows (connections) linked to this node
        connectionArrows.entrySet().removeIf(entry -> {
            String connectionKey = entry.getKey();
            Arrow arrow = entry.getValue();
            if (connectionKey.startsWith(node.getId() + "->") || connectionKey.endsWith("->" + node.getId())) {
                workspacePane.getChildren().remove(arrow);
                return true;
            }
            return false;
        });

        // Step 3: Remove the node's visual from the internal node view map
        nodeViewsMap.remove(node.getId());

        // Step 4: Push the deletion as an undoable action to enable rollback
        undoStack.push(new UndoableAction(UndoableAction.ActionType.DELETE_NODE, node, connectionsToSave)); // Command Pattern, Code Reuse

        // Step 5: Clear outgoing connections from model (cleanup)
        node.getOutgoingConnections().clear();

        // Step 6: Refresh the minimap to reflect the new canvas state
        controller.updateMiniMap(); // Abstraction: UI logic remains separated
    }
}
