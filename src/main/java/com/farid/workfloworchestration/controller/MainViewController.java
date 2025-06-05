package com.farid.workfloworchestration.controller;

import com.farid.workfloworchestration.command.ConnectNodesCommand;
import com.farid.workfloworchestration.command.WorkflowInvoker;
import com.farid.workfloworchestration.exception.InvalidWorkflowException;
import com.farid.workfloworchestration.execution.GenericExecutionLogger;
import com.farid.workfloworchestration.model.*;
import com.farid.workfloworchestration.service.WorkflowValidationService;
import com.farid.workfloworchestration.view.NodeCreationDialog;
import com.farid.workfloworchestration.model.UndoableAction;
import com.farid.workfloworchestration.view.Arrow;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.Group;
import javafx.scene.input.ScrollEvent;
import javafx.geometry.Pos;
import javafx.scene.shape.Rectangle;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MainViewController
 * <p>
 * This controller serves as the main bridge between the GUI (defined in `main-view.fxml`)
 * and the application's workflow logic. It handles user interactions such as node creation,
 * connection, execution, undo/redo, zooming, and sidebar updates.
 *
 * <p><strong>Key Responsibilities:</strong></p>
 * <ul>
 *   <li>Handles JavaFX UI events and dispatches them to the model/controller layer.</li>
 *   <li>Manipulates the workspacePane (the visual canvas).</li>
 *   <li>Interacts with the MainController for backend logic and services.</li>
 * </ul>
 *
 * <p><strong>OOP Concepts Implemented:</strong></p>
 * <ul>
 *   <li><strong>Encapsulation:</strong> GUI components and logic are wrapped in a class with private fields.</li>
 *   <li><strong>Abstraction:</strong> Separates GUI from business logic by delegating execution to MainController.</li>
 *   <li><strong>Dependency Injection:</strong> MainController is injected and used instead of creating directly.</li>
 *   <li><strong>High Cohesion:</strong> Dedicated solely to GUI logic (SRP - Single Responsibility Principle).</li>
 * </ul>
 */
public class MainViewController {

    // === Top toolbar buttons ===

    @FXML
    private Button createNodeButton;           // Button for creating a new node
    @FXML
    private Button connectNodesButton;         // Button to connect two selected nodes
    @FXML
    private Button executeWorkflowButton;      // Button to execute the full workflow
    @FXML
    private Button saveWorkflowButton;         // Button to save the current workflow state
    @FXML
    private Button loadWorkflowButton;         // Button to load an existing workflow
    @FXML
    private Button clearWorkspaceButton;       // Clears all nodes and connections from the canvas

    // === Zoom and undo/redo functionality ===

    @FXML
    private Button resetZoomButton;            // Resets zoom level to 100%
    @FXML
    private Button undoButton;                 // Undo last action (connection, node)
    @FXML
    private Button redoButton;                 // Redo previously undone action

    // === Canvas components ===

    @FXML
    private Pane miniMapPane;                  // Minimap view for workspace navigation
    @FXML
    private Pane workspacePane;                // Main pane where nodes and connections are placed

    @FXML
    private BorderPane rootPane;               // The root layout of the main view (FXML-injected)

    // === Sidebar components (node details) ===

    @FXML
    private VBox sidebar;                      // Right sidebar container
    @FXML
    private Label sidebarNodeId;               // Displays selected node's ID
    @FXML
    private TextField sidebarNodeNameField;    // Editable field for node name
    @FXML
    private Label sidebarNodeType;             // Label for node type (deprecated, replaced by ComboBox)
    @FXML
    private ComboBox<NodeType> sidebarNodeTypeCombo;  // Dropdown to change node type
    @FXML
    private TextArea sidebarDetailsArea;       // Field to write and store node-specific descriptions

    @FXML
    private Label sidebarExecutionStatus;      // Label that shows execution status (Running, Done, etc.)
    @FXML
    private Label sidebarExecutionStatusLabel; // [Optional] Duplicate or placeholder for status styling

    // === Workspace title editing ===

    @FXML
    private TextField workspaceTitleField;     // Title field for the current workflow (read-only or editable)
    @FXML
    private Button zoomInButton;               // Zoom-in button
    @FXML
    private Button zoomOutButton;              // Zoom-out button

    // === Execution output ===

    @FXML
    private TextArea executionLogArea;         // Displays workflow execution logs for each node





// === Logic & Interaction Fields ===

    /** Injected main controller that handles application logic. (Dependency Injection) */
    private MainController mainController;

    /** Outer wrapper group used for zooming and panning. */
    private Group workspaceGroup;

    /** Inner content group inside the workspaceGroup, holds nodes and arrows. */
    private Group contentGroup;

    /** ScrollPane containing the entire workspace for scroll and zoom control. */
    private ScrollPane scrollPane;

    /** Current zoom level. Default is 100% (1.0). */
    private double scaleValue = 1.0;

    /** Controls how fast zooming happens via scroll wheel or buttons. */
    private final double zoomIntensity = 0.01;

// === Panning & Selection Fields ===

    /** Last mouse X coordinate (used for dragging/panning). */
    private double lastMouseX;

    /** Last mouse Y coordinate (used for dragging/panning). */
    private double lastMouseY;

    /** Flag to determine if the user is currently panning. */
    private boolean isPanning = false;

    /** Selection rectangle used for selecting multiple nodes. */
    private javafx.scene.shape.Rectangle selectionRectangle;

    /** List of currently selected node views (used for grouping/selecting). */
    private final List<VBox> selectedNodes = new ArrayList<>();

    /** Starting X coordinate for selection rectangle. */
    private double rectStartX;

    /** Starting Y coordinate for selection rectangle. */
    private double rectStartY;

    /**
     * Selects all nodes inside the current selection rectangle.
     * Adds a 'selected' CSS class and updates internal tracking.
     * <p>
     * OOP Concept: Encapsulation — internal logic hidden from external access.
     */
    private void selectNodesInsideRectangle() {
        for (VBox nodeView : nodeViewsMap.values()) {
            if (selectionRectangle.getBoundsInParent().intersects(nodeView.getBoundsInParent())) {
                if (!selectedNodes.contains(nodeView)) {
                    selectedNodes.add(nodeView);
                }
                if (!nodeView.getStyleClass().contains("selected")) {
                    nodeView.getStyleClass().add("selected");
                }
            }
        }
    }

// === Data Structures for Workflow Management ===

    /** Maps node IDs to their corresponding JavaFX view (used for UI rendering). */
    private final Map<String, VBox> nodeViewsMap = new HashMap<>();

    /** Maps connection keys (source->target) to Arrow views for fast lookup and deletion. */
    private final Map<String, Arrow> connectionArrows = new HashMap<>();

    /** Undo stack for storing reversible actions (UndoableAction pattern). */
    private final Stack<UndoableAction> undoStack = new Stack<>();

    /** Redo stack for reapplying previously undone actions. */
    private final Stack<UndoableAction> redoStack = new Stack<>();

    /** Tracks all arrows visually displayed (for redraw/update purposes). */
    private final List<Arrow> visualArrows = new ArrayList<>();

    /** Command pattern invoker to manage execution of actions like connecting nodes. */
    private final WorkflowInvoker workflowInvoker = new WorkflowInvoker();

    /** Reference to the current workflow file (used for saving/loading). */
    private java.io.File currentWorkflowFile;

    /** Title of the current workflow shown in the UI. */
    private String currentWorkflowFilename = "Untitled Workflow";

// === UI Event Handlers ===

    /**
     * Automatically arranges the nodes in a readable layout.
     * Triggered when user clicks the Auto Arrange button.
     * <p>
     * OOP Principle: Abstraction — UI delegates the layout logic to internal methods.
     */
    @FXML
    private void handleAutoArrange() {
        autoArrangeNodes();
    }

    /**
     * Resets zoom level back to 100%.
     * Called when user clicks "Reset Zoom" button.
     */
    @FXML
    private void handleResetZoom() {
        scaleValue = 1.0;
        contentGroup.setScaleX(scaleValue);
        contentGroup.setScaleY(scaleValue);
        System.out.println("Zoom reset to 100%");
    }

// === Canvas Utility Methods ===

    /**
     * Adds a node to the visual canvas.
     * Internally delegates to addNodeToWorkspace (which handles positioning).
     *
     * @param node The WorkflowNode object to be added
     */
    public void addNodeToCanvas(WorkflowNode node) {
        addNodeToWorkspace(node);
    }

    /**
     * Deletes a node from the visual canvas and workflow.
     * <p>
     * - Removes it from the canvas (workspacePane)
     * - Removes connected arrows
     * - Removes it from internal data structures
     * - Updates the minimap
     *
     * @param node The WorkflowNode to delete
     */
    public void deleteNodeFromCanvas(WorkflowNode node) {
        VBox nodeView = nodeViewsMap.get(node.getId());
        if (nodeView != null) {
            workspacePane.getChildren().remove(nodeView);
        }

        // Remove arrows linked to this node
        connectionArrows.entrySet().removeIf(entry -> {
            Arrow arrow = entry.getValue();
            if (entry.getKey().startsWith(node.getId() + "->") || entry.getKey().endsWith("->" + node.getId())) {
                workspacePane.getChildren().remove(arrow);
                return true;
            }
            return false;
        });

        // Remove from map and workflow service
        nodeViewsMap.remove(node.getId());
        mainController.getWorkflowService().removeNode(node); // Service must support this method
        updateMiniMap();
    }

    /**
     * Connects two nodes both visually and logically.
     * Draws an arrow, adds it to internal maps, and updates workflow state.
     *
     * @param source The source node
     * @param target The target node
     * @param label  Optional label (e.g. "YES", "NO")
     */
    public void connectNodes(WorkflowNode source, WorkflowNode target, String label) {
        // 1. Draw arrow
        drawConnectionBetween(source, target);

        // 2. Generate key
        String connectionKey = source.getId() + "->" + target.getId();

        // 3. Create and cache arrow if not yet created
        Arrow arrow = connectionArrows.get(connectionKey);
        if (arrow == null) {
            VBox sourceView = nodeViewsMap.get(source.getId());
            VBox targetView = nodeViewsMap.get(target.getId());

            double startX = sourceView != null ? sourceView.getLayoutX() : 0;
            double startY = sourceView != null ? sourceView.getLayoutY() : 0;
            double endX = targetView != null ? targetView.getLayoutX() : 0;
            double endY = targetView != null ? targetView.getLayoutY() : 0;

            arrow = new Arrow(startX, startY, endX, endY);
            workspacePane.getChildren().add(arrow);
            connectionArrows.put(connectionKey, arrow);
        }

        // 4. Apply label
        if (label != null && !label.isEmpty()) {
            arrow.setLabel(label);
        }

        // 5. Update logic-level connection in workflow
        mainController.getWorkflowService().addConnection(new WorkflowConnection(source, target));
    }





    /**
     * Clears the entire workflow workspace after user confirmation.
     * This includes:
     * - UI nodes
     * - Arrows
     * - Internal maps
     * - Workflow data
     * - Sidebar content
     * - Execution log
     * - Minimap
     * <p>
     * OOP Concepts:
     * - Encapsulation: UI components and workflow logic are hidden inside this method.
     * - Abstraction: Provides a clean user-level command while hiding internal structure clearing.
     * - SRP: Handles only the workspace clearing logic, nothing else.
     */
    @FXML
    private void handleClearWorkspace() {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Clear Workspace");
        confirmationDialog.setHeaderText("Are you sure you want to clear the entire workspace?");
        confirmationDialog.setContentText("This action will remove all nodes and connections.");

        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // ✅ Clear canvas visuals
            workspacePane.getChildren().clear();
            nodeViewsMap.clear();
            connectionArrows.clear();

            // ✅ Clear backend workflow logic
            mainController.clearWorkflow();

            // ✅ Clear execution logs from UI
            if (executionLogArea != null) {
                executionLogArea.clear();
            }

            // ✅ Reset sidebar fields
            if (sidebarNodeId != null) sidebarNodeId.setText("");
            if (sidebarNodeNameField != null) sidebarNodeNameField.setText("");
            if (sidebarNodeTypeCombo != null) sidebarNodeTypeCombo.setValue(null);
            if (sidebarDetailsArea != null) sidebarDetailsArea.setText("");
            if (sidebarExecutionStatusLabel != null) {
                sidebarExecutionStatusLabel.setText("Not executed");
                sidebarExecutionStatusLabel.setStyle("-fx-text-fill: gray;");
            }

            // ✅ Clear sidebar selection
            selectedSidebarNode = null;

            // ✅ Update visual minimap
            updateMiniMap();

            System.out.println("✅ Workspace and logs cleared successfully!");
        } else {
            System.out.println("⚠️ Clear workspace canceled by user.");
        }
    }

// === Sidebar State ===

    /** The node currently selected in the sidebar. Used for editing and updates. */
    private WorkflowNode selectedSidebarNode;

    /** Whether the user is in "Connect Nodes" mode. */
    private boolean connectModeActive = false;

    /** Stores the first selected VBox during connection selection. */
    private VBox firstSelectedNodeView = null;

    /** Stores the first selected WorkflowNode during connection selection. */
    private WorkflowNode firstSelectedWorkflowNode = null;

    /** Logger for general GUI-related logging or debugging. */
    private static final Logger LOGGER = Logger.getLogger(MainViewController.class.getName());

    /**
     * Sets the currently selected node for the sidebar editor.
     * Enables external controllers to update the sidebar dynamically.
     *
     * @param node The node to select
     */
    public void setSelectedSidebarNode(WorkflowNode node) {
        this.selectedSidebarNode = node;
    }

    /**
     * Temporarily highlights an arrow with a glowing effect,
     * then continues execution to the next node after animation.
     *
     * @param arrow     The arrow to highlight
     * @param nextNode  The next node to execute after glow
     * <p>
     * OOP Concepts:
     * - Abstraction: Visual feedback is hidden in a method.
     * - Composition: Uses JavaFX animation and effect classes.
     */
    private void highlightArrowThenContinue(Arrow arrow, WorkflowNode nextNode) {
        if (arrow == null || nextNode == null) return;

        // 🌟 Glowing blue effect for the arrow
        DropShadow glow = new DropShadow();
        glow.setColor(javafx.scene.paint.Color.CORNFLOWERBLUE);
        glow.setRadius(15);
        glow.setSpread(0.4);

        Line arrowLine = arrow.getLine();
        arrowLine.setEffect(glow);
        arrow.setArrowColor(Color.CORNFLOWERBLUE);

        // ✨ Pulsing animation to enhance visibility
        Timeline glowTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> glow.setRadius(15)),
                new KeyFrame(Duration.seconds(0.25), e -> glow.setRadius(25)),
                new KeyFrame(Duration.seconds(0.5), e -> glow.setRadius(15))
        );
        glowTimeline.setCycleCount(2); // Pulse once

        glowTimeline.setOnFinished(event -> {
            arrowLine.setEffect(null);
            arrow.setArrowColor(Color.BLACK); // Revert to normal
            if (!visitedNodesGUI.contains(nextNode.getId())) {
                executeAndHighlightNode(nextNode);
            }
        });

        glowTimeline.play();
    }

// === Title Editing Buttons (Toggle between edit and save) ===

    @FXML private Button editTitleButton;
    @FXML private Button saveTitleButton;

    /**
     * Allows the user to edit the workflow title.
     * Enables the text field and shows the Save button.
     */
    @FXML
    private void handleEditTitle() {
        workspaceTitleField.setEditable(true);
        workspaceTitleField.requestFocus();
        editTitleButton.setVisible(false);
        editTitleButton.setManaged(false);
        saveTitleButton.setVisible(true);
        saveTitleButton.setManaged(true);
    }

    /**
     * Saves the updated workflow title and disables the input field.
     * If the user entered an empty title, fallback to "Untitled".
     */
    @FXML
    private void handleRenameWorkflow() {
        String newTitle = workspaceTitleField.getText().trim();
        if (!newTitle.isEmpty()) {
            System.out.println("✅ Title changed to: " + newTitle);
        } else {
            workspaceTitleField.setText("Workflow: Untitled");
        }

        // Lock field and restore UI toggle
        workspaceTitleField.setEditable(false);
        saveTitleButton.setVisible(false);
        saveTitleButton.setManaged(false);
        editTitleButton.setVisible(true);
        editTitleButton.setManaged(true);
    }





    /**
     * Executes a node based on its type.
     * <p>
     * Handles three execution paths:
     * - Skips non-executable types (e.g., START, END)
     * - Evaluates condition nodes with branching logic
     * - Executes regular executable nodes like TASK, INFERENCE
     * <p>
     * OOP Concepts:
     * - Polymorphism: Uses `instanceof` to dispatch logic based on subclass type
     * - Abstraction: Execution logic encapsulated away from UI
     * - Encapsulation: Execution state hidden inside node classes
     */
    private void executeNode(WorkflowNode node) {
        NodeType type = node.getType();

        if (type == NodeType.OUTPUT || type == NodeType.DATA || type == NodeType.START || type == NodeType.END) {
            return; // Skip passive nodes
        }

        // Handle conditional branching nodes
        if (node instanceof ConditionNode conditionNode) {
            Map<String, String> dummyContext = new HashMap<>();
            evaluateConditionNode(conditionNode, dummyContext);
            return;
        }

        // Handle executable node types
        if (node instanceof ExecutableNode) {
            System.out.println("✅ Executing node: " + node.getName());
            try {
                node.execute(); // Polymorphic call
            } catch (InvalidWorkflowException e) {
                System.err.println("❌ Execution failed for node " + node.getName() + ": " + e.getMessage());
            }
            return;
        }

        // Fallback for unsupported node types
        System.out.println("⚠️ Skipped node '" + node.getName() + "' -- no execution logic for type: " + type);
    }

    /**
     * Evaluates a condition node, decides on YES/NO branch, and continues execution.
     * <p>
     * OOP Concepts:
     * - Abstraction: Hides condition evaluation logic from the caller
     * - Recursion: Calls executeNode() on the resulting branch
     */
    void evaluateConditionNode(ConditionNode node, Map<String, String> context) {
        System.out.println("Evaluating condition: " + node.getConditionExpression());

        boolean result = Math.random() > 0.5; // Mocked logic (can be replaced with real context evaluation)
        System.out.println("Condition result: " + (result ? "YES" : "NO"));

        WorkflowNode next = result ? node.getYesTarget() : node.getNoTarget();

        if (next != null) {
            System.out.println("Proceeding to node: " + next.getName());

            VBox nodeView = nodeViewsMap.get(next.getId());
            if (nodeView != null) {
                nodeView.getStyleClass().add("selected"); // Optional: highlight next node
            }

            executeNode(next); // Recursively proceed
        } else {
            System.out.println("No target node connected for this condition result.");
        }
    }

    /**
     * Updates arrow visuals for a given node.
     * Especially important for CONDITION nodes to update YES/NO labels.
     * <p>
     * OOP Concepts:
     * - Polymorphism: Special case for ConditionNode subclass
     * - Encapsulation: Label access via FXML lookup
     */
    private void updateArrowsForNode(WorkflowNode node) {
        for (WorkflowConnection conn : mainController.getWorkflowService().getConnectionsFrom(node)) {
            Arrow arrow = connectionArrows.get(node.getId() + "->" + conn.getTargetNode().getId());
            if (arrow != null) {
                // Positions are bound — no need to manually update
            }
        }

        for (WorkflowConnection conn : mainController.getWorkflowService().getConnectionsTo(node)) {
            Arrow arrow = connectionArrows.get(conn.getSourceNode().getId() + "->" + node.getId());
            if (arrow != null) {
                // Arrow is auto-bound
            }

            // Special case: Update labels on condition node
            if (node.getNodeType() == NodeType.CONDITION) {
                VBox nodeView = nodeViewsMap.get(node.getId());
                if (nodeView != null) {
                    Label yesLabel = (Label) nodeView.lookup("#yesTargetLabel");
                    Label noLabel = (Label) nodeView.lookup("#noTargetLabel");

                    ConditionNode conditionNode = (ConditionNode) node;
                    WorkflowNode yesTarget = conditionNode.getYesTarget();
                    WorkflowNode noTarget = conditionNode.getNoTarget();

                    String yesTargetName = (yesTarget == null) ? "(Unconnected)" : yesTarget.getName();
                    String noTargetName = (noTarget == null) ? "(Unconnected)" : noTarget.getName();

                    if (yesLabel != null) yesLabel.setText(yesTargetName);
                    if (noLabel != null) noLabel.setText(noTargetName);
                }
            }
        }
    }

    /**
     * Draws an arrow between two nodes and binds it to their position.
     * <p>
     * OOP Concepts:
     * - Composition: Arrow object is composed of Line + optional label
     * - Abstraction: Drawing details are encapsulated
     */
    private void drawConnectionBetween(WorkflowNode source, WorkflowNode target) {
        if (source == null || target == null) return;

        VBox sourceView = nodeViewsMap.get(source.getId());
        VBox targetView = nodeViewsMap.get(target.getId());

        if (sourceView == null || targetView == null) return;

        Arrow arrow = new Arrow(0, 0, 0, 0);

        // Bind arrow to follow source/target nodes
        arrow.getLine().startXProperty().bind(sourceView.layoutXProperty().add(sourceView.widthProperty().divide(2)));
        arrow.getLine().startYProperty().bind(sourceView.layoutYProperty().add(sourceView.heightProperty().divide(2)));
        arrow.getLine().endXProperty().bind(targetView.layoutXProperty().add(targetView.widthProperty().divide(2)));
        arrow.getLine().endYProperty().bind(targetView.layoutYProperty().add(targetView.heightProperty().divide(2)));

        visualArrows.add(arrow);
        workspacePane.getChildren().add(0, arrow);
        connectionArrows.put(source.getId() + "->" + target.getId(), arrow);
    }

    /**
     * Detects cycles in the current workflow graph.
     * <p>
     * OOP Concepts:
     * - Recursion: Uses DFS to find cycles
     * - Encapsulation: Graph traversal logic hidden from UI
     * - SRP: Sole responsibility is to detect cycles, not resolve them
     *
     * @return true if a cycle is found, false otherwise
     */
    private boolean detectCycles() {
        Map<String, Boolean> visited = new HashMap<>();
        Map<String, Boolean> recStack = new HashMap<>();

        for (WorkflowNode node : mainController.getWorkflowService().getAllNodes()) {
            visited.put(node.getId(), false);
            recStack.put(node.getId(), false);
        }

        for (WorkflowNode node : mainController.getWorkflowService().getAllNodes()) {
            if (detectCycleUtil(node, visited, recStack)) {
                return true; // Cycle found
            }
        }
        return false;
    }


    /**
     * Recursively checks for cycles in the workflow graph using Depth-First Search (DFS).
     *
     * @param node The current node being visited
     * @param visited Map tracking all visited nodes
     * @param recStack Map tracking nodes in the current recursion stack
     * @return true if a cycle is detected, false otherwise
     * <p>
     * OOP Concepts:
     * - **Abstraction**: This logic abstracts graph traversal from the UI.
     * - **Encapsulation**: Internal traversal state (visited/recStack) is kept private to the method.
     */
    private boolean detectCycleUtil(WorkflowNode node, Map<String, Boolean> visited, Map<String, Boolean> recStack) {
        if (node == null) return false;

        String nodeId = node.getId();

        // If this node is already in the current path, we found a cycle
        if (recStack.get(nodeId)) {
            return true;
        }

        // If this node was already visited and not in the current path, skip it
        if (visited.get(nodeId)) {
            return false;
        }

        // Mark the node as visited and add it to the recursion stack
        visited.put(nodeId, true);
        recStack.put(nodeId, true);

        // Recursively check all connected (outgoing) nodes
        List<WorkflowConnection> outgoingConnections = mainController.getWorkflowService().getConnectionsFrom(node);
        if (outgoingConnections != null) {
            for (WorkflowConnection connection : outgoingConnections) {
                WorkflowNode neighbor = connection.getTargetNode();
                if (detectCycleUtil(neighbor, visited, recStack)) {
                    return true;
                }
            }
        }

        // Backtrack: remove the node from recursion stack
        recStack.put(nodeId, false);
        return false;
    }

    /**
     * Validates the entire workflow before execution.
     * Checks include:
     * 1. Every node is connected.
     * 2. There are no circular dependencies.
     * 3. Each node type satisfies specific connection rules.
     *
     * @return true if workflow is valid, false otherwise
     * <p>
     * OOP Concepts:
     * - **Modularity**: This method encapsulates all validation logic in one unit.
     * - **Abstraction**: The user doesn’t see how validation works, only the outcome.
     * - **Information Hiding**: Visual feedback and error logging are internal.
     */
    private boolean validateWorkflow() {
        boolean valid = true;
        StringBuilder errorMessages = new StringBuilder();

        //  Clear any previous red highlights from nodes
        for (VBox nodeView : nodeViewsMap.values()) {
            nodeView.setStyle(nodeView.getStyle().replace("-fx-border-color: red;", ""));
        }

        //  Check 1: Ensure each node is connected (no isolated nodes)
        for (WorkflowNode node : mainController.getWorkflowService().getAllNodes()) {
            List<WorkflowConnection> outgoing = mainController.getWorkflowService().getConnectionsFrom(node);
            List<WorkflowConnection> incoming = mainController.getWorkflowService().getConnectionsTo(node);

            if ((outgoing == null || outgoing.isEmpty()) && (incoming == null || incoming.isEmpty())) {
                valid = false;
                errorMessages.append("Node \"").append(node.getName()).append("\" is not connected to anything.\n");

                // Highlight unconnected node visually
                VBox nodeView = nodeViewsMap.get(node.getId());
                if (nodeView != null) {
                    nodeView.setStyle(nodeView.getStyle() + "-fx-border-color: red; -fx-border-width: 3px;");
                }
            }
        }

        //  Check 2: Detect any cycles in the graph
        if (detectCycles()) {
            valid = false;
            errorMessages.append("Cycle detected in the workflow! Execution is not allowed.\n");
        }

        //  Check 3: Enforce node-specific connection rules
        for (WorkflowNode node : mainController.getWorkflowService().getAllNodes()) {
            List<WorkflowConnection> outgoing = mainController.getWorkflowService().getConnectionsFrom(node);

            switch (node.getNodeType()) {
                case TASK:
                    if (outgoing.size() != 1) {
                        valid = false;
                        errorMessages.append("Task Node \"").append(node.getName()).append("\" must have exactly one outgoing connection.\n");

                        VBox nodeView = nodeViewsMap.get(node.getId());
                        if (nodeView != null) {
                            nodeView.setStyle(nodeView.getStyle() + "-fx-border-color: red; -fx-border-width: 3px;");
                        }
                    }
                    break;

                case CONDITION:
                    if (outgoing.size() != 2) {
                        valid = false;
                        errorMessages.append("Condition Node \"").append(node.getName()).append("\" must have exactly two outgoing connections (e.g., True/False paths).\n");

                        VBox nodeView = nodeViewsMap.get(node.getId());
                        if (nodeView != null) {
                            nodeView.setStyle(nodeView.getStyle() + "-fx-border-color: red; -fx-border-width: 3px;");
                        }
                    }
                    break;

                case PREDICTION:
                    if (outgoing.size() != 1) {
                        valid = false;
                        errorMessages.append("Prediction Node \"").append(node.getName()).append("\" must have exactly one outgoing connection.\n");

                        VBox nodeView = nodeViewsMap.get(node.getId());
                        if (nodeView != null) {
                            nodeView.setStyle(nodeView.getStyle() + "-fx-border-color: red; -fx-border-width: 3px;");
                        }
                    }
                    break;
            }
        }

        //  If any issues were found, display them in an alert box
        if (!valid) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Workflow validation failed!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }

        return valid;
    }


    /**
     * Handles the Undo functionality for workflow actions (node creation, deletion, movement, connection).
     * Pops the most recent action from the undo stack and reverses it. The action is then pushed to the redo stack.
     * <p>
     * OOP Concepts:
     * - **Encapsulation**: Action reversal logic is encapsulated inside each switch case.
     * - **Abstraction**: The user simply triggers 'undo' without knowing the internal reversal logic.
     * - **Command Pattern**: `UndoableAction` represents an abstract action that can be undone or redone.
     * - **Polymorphism**: Behavior changes based on `UndoableActionType` via `switch-case` (enum-driven control flow).
     */
    @FXML
    private void handleUndo() {
        if (!undoStack.isEmpty()) {
            // Get the last action performed
            UndoableAction action = undoStack.pop();

            switch (action.getActionType()) {

                //  Undo CREATE_NODE: Remove the node and its arrows
                case CREATE_NODE:
                    WorkflowNode nodeToRemove = action.getNode();
                    VBox nodeView = nodeViewsMap.get(nodeToRemove.getId());

                    if (nodeView != null) {
                        // 1. Remove all arrows connected to this node
                        connectionArrows.entrySet().removeIf(entry -> {
                            String key = entry.getKey();
                            Arrow arrow = entry.getValue();
                            if (key.startsWith(nodeToRemove.getId() + "->") || key.endsWith("->" + nodeToRemove.getId())) {
                                workspacePane.getChildren().remove(arrow);
                                return true;
                            }
                            return false;
                        });

                        // 2. Remove the node view from the workspace
                        workspacePane.getChildren().remove(nodeView);
                        nodeViewsMap.remove(nodeToRemove.getId());

                        System.out.println("Undo: Node created removed - " + nodeToRemove.getName());
                    }
                    break;

                //  Undo DELETE_NODE: Recreate the node and its saved connections
                case DELETE_NODE:
                    WorkflowNode deletedNode = action.getNode();
                    addNodeToWorkspace(deletedNode); // Redraw node

                    // Restore connections that existed before deletion
                    List<WorkflowConnection> savedConnections = action.getSavedConnections();
                    if (savedConnections != null) {
                        for (WorkflowConnection connection : savedConnections) {
                            WorkflowNode source = connection.getSourceNode();
                            WorkflowNode target = connection.getTargetNode();
                            if (source != null && target != null) {
                                mainController.connectNodes(source, target, null);
                                drawConnectionBetween(source, target);
                            }
                        }
                    }

                    System.out.println("Undo: Node deleted restored with its connections - " + deletedNode.getName());
                    break;

                //  Undo MOVE_NODE: Move node back to original position
                case MOVE_NODE:
                    WorkflowNode movedNode = action.getNode();
                    VBox movedNodeView = nodeViewsMap.get(movedNode.getId());
                    if (movedNodeView != null) {
                        movedNodeView.setLayoutX(action.getOldX());
                        movedNodeView.setLayoutY(action.getOldY());
                    }
                    System.out.println("Undo: Node moved back - " + movedNode.getName());
                    break;

                //  Undo CONNECT_NODES: Remove the connection (both logic and arrow)
                case CONNECT_NODES:
                    WorkflowNode source = action.getSourceNode();
                    WorkflowNode target = action.getTargetNode();
                    if (source != null && target != null) {
                        mainController.disconnectNodes(source, target); // Remove from logic
                        String connectionKey = source.getId() + "->" + target.getId();
                        Arrow arrow = connectionArrows.remove(connectionKey);
                        if (arrow != null) {
                            action.setConnectionLabel(arrow.getLabel()); // Save label for redo
                            workspacePane.getChildren().remove(arrow);   // Remove arrow from view
                        }
                        System.out.println("Undo: Connection removed between " + source.getName() + " and " + target.getName());
                    }
                    break;

                // Undo DISCONNECT_NODES: Restore the connection and draw its visual arrow
                case DISCONNECT_NODES:
                    WorkflowNode sourceNode = action.getSourceNode();
                    WorkflowNode targetNode = action.getTargetNode();
                    String restoredLabel = action.getConnectionLabel();

                    mainController.connectNodes(sourceNode, targetNode, restoredLabel); // Logic reconnect
                    drawConnectionBetween(sourceNode, targetNode); // Redraw arrow visually
                    System.out.println("Undo: Connection restored between " + sourceNode.getName() + " and " + targetNode.getName() + " with label: " + restoredLabel);
                    break;

                //  Unknown action type fallback
                default:
                    System.out.println("Undo: Unknown action type");
            }

            // Push the action to redo stack so it can be redone later
            redoStack.push(action);

            // ✅ Always refresh the minimap after any visual change
            updateMiniMap();
        }
    }



    /**
     * Automatically arranges all nodes in the workspace in a centered grid layout with animation.
     * This enhances visual clarity by organizing nodes evenly, especially after bulk imports or scattered placements.
     * <p>
     * Object-Oriented Concepts Demonstrated:
     * - **Encapsulation**: Method hides internal layout logic and exposes a clean interface.
     * - **Abstraction**: Animation and layout logic are abstracted away from the user.
     * - **Modularity**: Method is self-contained, making it reusable and testable.
     * - **Information Hiding**: Node position calculations are not exposed externally.
     */
    private void autoArrangeNodes() {
        int cols = 4; // Number of nodes per row
        int spacingX = 200; // Horizontal spacing between nodes
        int spacingY = 150; // Vertical spacing between nodes
        int nodeWidth = 150;  // Estimated width of a node
        int nodeHeight = 100; // Estimated height of a node

        int totalNodes = nodeViewsMap.size();
        int rows = (int) Math.ceil((double) totalNodes / cols);

        // Calculate total layout area dimensions
        double totalWidth = cols * spacingX;
        double totalHeight = rows * spacingY;

        // Center the layout in the workspace pane
        double centerX = workspacePane.getWidth() / 2;
        double centerY = workspacePane.getHeight() / 2;

        // Determine top-left starting point to center the grid
        double startX = centerX - (totalWidth / 2) + (spacingX - nodeWidth) / 2.0;
        double startY = centerY - (totalHeight / 2) + (spacingY - nodeHeight) / 2.0;

        int currentCol = 0;
        int currentRow = 0;

        // Group all node transitions for simultaneous animation
        javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition();

        for (VBox nodeView : nodeViewsMap.values()) {
            double newX = startX + currentCol * spacingX;
            double newY = startY + currentRow * spacingY;

            // Create a smooth transition from current position to new position
            javafx.animation.TranslateTransition transition =
                    new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.5), nodeView);

            transition.setToX(newX - nodeView.getLayoutX());
            transition.setToY(newY - nodeView.getLayoutY());

            parallelTransition.getChildren().add(transition);

            currentCol++;
            if (currentCol >= cols) {
                currentCol = 0;
                currentRow++;
            }
        }

        // Finalize layout after animation completes
        parallelTransition.setOnFinished(event -> {
            for (VBox nodeView : nodeViewsMap.values()) {
                double finalX = nodeView.getLayoutX() + nodeView.getTranslateX();
                double finalY = nodeView.getLayoutY() + nodeView.getTranslateY();

                nodeView.setLayoutX(finalX);
                nodeView.setLayoutY(finalY);
                nodeView.setTranslateX(0);
                nodeView.setTranslateY(0);
            }

            // Refresh the minimap to reflect updated positions
            updateMiniMap();
        });

        parallelTransition.play();
        System.out.println("Auto-arranging nodes centered with smooth animation!");
    }


    /**
     * Zooms into the content by increasing the scale value, with an upper limit of 150%.
     * This enhances the visibility of the workspace, especially for detailed node inspection.
     * <p>
     * OOP Concepts:
     * - **Encapsulation**: Internal zoom logic is encapsulated in a single method.
     * - **Information Hiding**: Users of the system don’t need to know how scaling is applied.
     */
    @FXML
    private void handleZoomIn() {
        scaleValue = Math.min(scaleValue + 0.1, 1.5); // Limit max zoom to 150%
        contentGroup.setScaleX(scaleValue);
        contentGroup.setScaleY(scaleValue);
        System.out.println("Zoomed in: " + (int)(scaleValue * 100) + "%");
    }

    /**
     * Zooms out of the content by decreasing the scale value, with a lower limit of 30%.
     * Useful for viewing the entire workflow when zoomed out.
     * <p>
     * OOP Concepts:
     * - **Encapsulation**: Scaling logic is hidden behind this single function.
     */
    @FXML
    private void handleZoomOut() {
        scaleValue = Math.max(scaleValue - 0.1, 0.3); // Limit min zoom to 30%
        contentGroup.setScaleX(scaleValue);
        contentGroup.setScaleY(scaleValue);
        System.out.println("Zoomed out: " + (int)(scaleValue * 100) + "%");
    }

    /**
     * Updates the mini-map to reflect the current layout of the nodes in the workspace.
     * Each node is represented by a scaled-down blue rectangle, giving the user an overview
     * of the entire workflow's structure.
     * <p>
     * OOP Concepts:
     * - **Modularity**: This method focuses solely on updating the minimap view.
     * - **Abstraction**: Users interact with the minimap but are abstracted from internal scaling logic.
     * - **Reusability**: This method is called after layout changes or undo actions.
     */
    public void updateMiniMap() {
        // Clear existing mini-map contents
        miniMapPane.getChildren().clear();

        // Get dimensions of the workspace and mini-map containers
        double workspaceWidth = workspacePane.getWidth();
        double workspaceHeight = workspacePane.getHeight();
        double miniMapWidth = miniMapPane.getWidth();
        double miniMapHeight = miniMapPane.getHeight();

        // Calculate scale ratio for minimap rendering
        double scaleX = miniMapWidth / workspaceWidth;
        double scaleY = miniMapHeight / workspaceHeight;
        double scale = Math.min(scaleX, scaleY); // Keep uniform scaling

        // Render each node as a blue rectangle in the mini-map
        for (VBox nodeView : nodeViewsMap.values()) {
            javafx.scene.shape.Rectangle miniNode = new javafx.scene.shape.Rectangle();
            miniNode.setWidth(nodeView.getWidth() * scale);
            miniNode.setHeight(nodeView.getHeight() * scale);
            miniNode.setLayoutX(nodeView.getLayoutX() * scale);
            miniNode.setLayoutY(nodeView.getLayoutY() * scale);
            miniNode.setFill(javafx.scene.paint.Color.BLUE); // Visual representation
            miniMapPane.getChildren().add(miniNode);
        }
    }


    /**
     * Redoes the last undone user action (if available), restoring the workflow to its next state.
     * Supported actions include: node creation, deletion, movement, and connection/disconnection between nodes.
     * <p>
     * OOP Concepts:
     * - Encapsulation: Action-specific redo logic is encapsulated within this method.
     * - Command Pattern: Uses stored `UndoableAction` objects to reverse prior undo operations.
     * - Abstraction: Internally handles visual and logical restoration without exposing complexity to the user.
     */
    @FXML
    private void handleRedo() {
        if (!redoStack.isEmpty()) {
            UndoableAction action = redoStack.pop();

            switch (action.getActionType()) {

                case CREATE_NODE:
                    // Re-add a previously undone node
                    WorkflowNode nodeToReAdd = action.getNode();
                    addNodeToWorkspace(nodeToReAdd);
                    System.out.println("Redo: Node re-added - " + nodeToReAdd.getName());
                    break;

                case DELETE_NODE:
                    // Re-delete a node that was previously restored
                    WorkflowNode nodeToDelete = action.getNode();
                    VBox viewToDelete = nodeViewsMap.get(nodeToDelete.getId());

                    if (viewToDelete != null) {
                        // Remove all connected arrows visually and logically
                        connectionArrows.entrySet().removeIf(entry -> {
                            String key = entry.getKey();
                            Arrow arrow = entry.getValue();
                            if (key.startsWith(nodeToDelete.getId() + "->") || key.endsWith("->" + nodeToDelete.getId())) {
                                workspacePane.getChildren().remove(arrow);
                                return true;
                            }
                            return false;
                        });

                        workspacePane.getChildren().remove(viewToDelete);
                        nodeViewsMap.remove(nodeToDelete.getId());

                        System.out.println("Redo: Node deleted again - " + nodeToDelete.getName());
                    }
                    break;

                case MOVE_NODE:
                    // Move node to its most recent position
                    WorkflowNode movedNode = action.getNode();
                    VBox movedNodeView = nodeViewsMap.get(movedNode.getId());
                    if (movedNodeView != null) {
                        movedNodeView.setLayoutX(action.getNewX());
                        movedNodeView.setLayoutY(action.getNewY());
                    }
                    System.out.println("Redo: Node moved to new position - " + movedNode.getName());
                    break;

                case CONNECT_NODES:
                    // Reconnect nodes after an undo
                    WorkflowNode source = action.getSourceNode();
                    WorkflowNode target = action.getTargetNode();
                    String label = action.getConnectionLabel(); // Label is reused

                    mainController.connectNodes(source, target, label); // Reconnect in model
                    drawConnectionBetween(source, target);              // Visual connection

                    // Restore the label visually
                    String connectionKey = source.getId() + "->" + target.getId();
                    Arrow arrow = connectionArrows.get(connectionKey);
                    if (arrow != null && label != null && !label.isEmpty()) {
                        arrow.setLabel(label);
                    }

                    System.out.println("Redo: Connection re-created between " + source.getName()
                            + " and " + target.getName() + " with label: " + label);
                    break;

                case DISCONNECT_NODES:
                    // Re-disconnect nodes that were reconnected during undo
                    WorkflowNode s = action.getSourceNode();
                    WorkflowNode t = action.getTargetNode();

                    mainController.disconnectNodes(s, t); // Logic-level removal
                    String connKey = s.getId() + "->" + t.getId();
                    Arrow removedArrow = connectionArrows.remove(connKey);
                    if (removedArrow != null) {
                        workspacePane.getChildren().remove(removedArrow);
                    }

                    System.out.println("Redo: Connection removed again between " + s.getName() + " and " + t.getName());
                    break;
            }

            // Push the redone action back to the undo stack
            undoStack.push(action);

            // Refresh minimap to reflect the new state
            updateMiniMap();
        }
    }



    /**
     * Adjusts the visual rotation of an arrowhead to match the angle of its corresponding line.
     * Ensures arrows point in the correct direction from source to target node.
     * <p>
     * OOP Concepts:
     * - Modularity: Rotation logic is separated into its own method for clarity and reuse.
     * - Abstraction: Users are abstracted from the geometric calculations.
     *
     * @param line The line representing the arrow shaft.
     * @param arrowHead The polygon representing the arrowhead.
     */
    private void updateArrowRotation(Line line, javafx.scene.shape.Polygon arrowHead) {
        double ex = line.getEndX();
        double ey = line.getEndY();
        double sx = line.getStartX();
        double sy = line.getStartY();

        // Calculate the angle in degrees between start and end point
        double angle = Math.atan2((ey - sy), (ex - sx)) * 180 / Math.PI;
        arrowHead.setRotate(angle);
    }


    /**
     * Opens a file chooser dialog and triggers saving the current workflow to a selected JSON file.
     * If a file is selected, it delegates the actual writing process to {@code saveWorkflowToFile}.
     * Also updates the title field to reflect the saved file name.
     */
    @FXML
    private void handleSaveWorkflow() {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Workflow");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json")
            );

            java.io.File file = fileChooser.showSaveDialog(scrollPane.getScene().getWindow());

            if (file != null) {
                saveWorkflowToFile(file);

                // Update the workspace title with the saved file name
                currentWorkflowFile = file;
                workspaceTitleField.setText("📁 " + file.getName());
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while saving the workflow", e);
        }
    }



    /**
     * Saves the current workflow to a JSON file on disk.
     * This method serializes both node data (ID, name, type, coordinates) and connection data (source, target, optional label).
     *
     * @param file The destination file to which the workflow will be saved.
     */
    private void saveWorkflowToFile(java.io.File file) {
        try {
            org.json.JSONObject workflowJson = new org.json.JSONObject();

            // Serialize node information
            org.json.JSONArray nodesArray = new org.json.JSONArray();
            for (WorkflowNode node : nodeViewsMap.keySet().stream().map(mainController.getWorkflowService()::findNodeById).toList()) {
                if (node == null) continue;
                VBox nodeView = nodeViewsMap.get(node.getId());

                org.json.JSONObject nodeJson = new org.json.JSONObject();
                nodeJson.put("id", node.getId());
                nodeJson.put("name", node.getName());
                nodeJson.put("type", node.getNodeType().toString());
                nodeJson.put("x", nodeView.getLayoutX());
                nodeJson.put("y", nodeView.getLayoutY());
                nodesArray.put(nodeJson);
            }

            // Serialize connection information
            org.json.JSONArray connectionsArray = new org.json.JSONArray();
            for (WorkflowConnection connection : mainController.getWorkflowService().getAllConnections()) {
                org.json.JSONObject connJson = new org.json.JSONObject();
                connJson.put("sourceId", connection.getSourceNode().getId());
                connJson.put("targetId", connection.getTargetNode().getId());

                // Save label (if present) for the arrow
                String connectionKey = connection.getSourceNode().getId() + "->" + connection.getTargetNode().getId();
                if (connectionArrows.containsKey(connectionKey)) {
                    String label = connectionArrows.get(connectionKey).getLabel();
                    if (label != null && !label.isEmpty()) {
                        connJson.put("label", label);
                    }
                }

                connectionsArray.put(connJson);
            }

            // Final JSON object
            workflowJson.put("nodes", nodesArray);
            workflowJson.put("connections", connectionsArray);

            // Write to file with pretty-print formatting
            java.nio.file.Files.writeString(file.toPath(), workflowJson.toString(4));
            System.out.println("Workflow saved successfully!");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while saving the workflow", e);
        }
    }


    /**
     * Opens a file chooser dialog to load a previously saved workflow from a JSON file.
     * Once the user selects a valid file, it delegates the actual deserialization to {@code loadWorkflowFromFile}.
     * Also updates the workspace title field to reflect the loaded file name.
     */
    @FXML
    private void handleLoadWorkflow() {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Load Workflow");
            fileChooser.getExtensionFilters().add(
                    new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json")
            );

            java.io.File file = fileChooser.showOpenDialog(scrollPane.getScene().getWindow());

            if (file != null) {
                loadWorkflowFromFile(file);

                currentWorkflowFile = file;
                workspaceTitleField.setText("📂 " + file.getName());
            }

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading the workflow", e);
        }
    }



    /**
     * Loads a previously saved workflow from a given JSON file.
     * This method reconstructs the visual nodes, their positions, and their connections
     * using the serialized data found in the file.
     *
     * @param file The JSON file containing the saved workflow structure.
     */
    private void loadWorkflowFromFile(java.io.File file) {
        try {
            String content = java.nio.file.Files.readString(file.toPath());
            org.json.JSONObject workflowJson = new org.json.JSONObject(content);

            // Clear current state before loading
            workspacePane.getChildren().clear();
            nodeViewsMap.clear();
            mainController.clearWorkflow();

            // Load and place all nodes
            org.json.JSONArray nodesArray = workflowJson.getJSONArray("nodes");
            for (int i = 0; i < nodesArray.length(); i++) {
                org.json.JSONObject nodeJson = nodesArray.getJSONObject(i);
                NodeType type = NodeType.valueOf(nodeJson.getString("type"));
                String id = nodeJson.getString("id");
                String name = nodeJson.getString("name");
                double x = nodeJson.getDouble("x");
                double y = nodeJson.getDouble("y");

                WorkflowNode node = mainController.createNode(type, id, name);
                addNodeToWorkspace(node);
                VBox nodeView = nodeViewsMap.get(node.getId());
                if (nodeView != null) {
                    nodeView.setLayoutX(x);
                    nodeView.setLayoutY(y);
                }
            }

            // Load and draw all connections
            org.json.JSONArray connectionsArray = workflowJson.getJSONArray("connections");
            for (int i = 0; i < connectionsArray.length(); i++) {
                org.json.JSONObject connJson = connectionsArray.getJSONObject(i);
                String sourceId = connJson.getString("sourceId");
                String targetId = connJson.getString("targetId");

                WorkflowNode sourceNode = mainController.getWorkflowService().findNodeById(sourceId);
                WorkflowNode targetNode = mainController.getWorkflowService().findNodeById(targetId);

                if (sourceNode != null && targetNode != null) {
                    mainController.connectNodes(sourceNode, targetNode, null);

                    VBox sourceView = nodeViewsMap.get(sourceId);
                    VBox targetView = nodeViewsMap.get(targetId);

                    if (sourceView != null && targetView != null) {
                        Arrow arrow = new Arrow(
                                sourceView.getLayoutX() + sourceView.getWidth() / 2,
                                sourceView.getLayoutY() + sourceView.getHeight() / 2,
                                targetView.getLayoutX() + targetView.getWidth() / 2,
                                targetView.getLayoutY() + targetView.getHeight() / 2
                        );

                        // Restore label if it was saved
                        if (connJson.has("label")) {
                            arrow.setLabel(connJson.getString("label"));
                        }

                        workspacePane.getChildren().add(0, arrow); // Render below nodes
                        String connectionKey = sourceId + "->" + targetId;
                        connectionArrows.put(connectionKey, arrow);
                    }
                }
            }

            updateMiniMap();

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading the workflow", e);
        }
    }



    /**
     * Opens a dialog to edit the name of a selected workflow node.
     * If a new name is provided, both the internal data and the visual label are updated.
     *
     * @param node     The workflow node to be renamed.
     * @param nodeView The visual representation (VBox) of the node.
     */
    private void editNode(WorkflowNode node, VBox nodeView) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(node.getName());
        dialog.setTitle("Edit Node");
        dialog.setHeaderText("Editing Node: " + node.getName());
        dialog.setContentText("Enter new name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            node.setName(newName);

            Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");
            if (nameLabel != null) {
                nameLabel.setText(newName);
            }

            System.out.println("Node renamed to: " + newName);
        });
    }


    /**
     * Injects the MainController instance into this controller.
     * This enables coordination between the view layer and the underlying workflow logic.
     *
     * @param mainController the central controller for workflow logic
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    /**
     * Initializes the main view controller after all @FXML-injected elements are loaded.
     * Sets up event handlers for UI buttons, configures sidebar elements, and initializes the workspace.
     */
    @FXML
    private void initialize() {
        // Set initial size for the workflow canvas
        workspacePane.setPrefSize(800, 600);  // or any reasonable size

        // === BUTTON ACTIONS ===
        // Attach button click handlers to corresponding controller methods
        createNodeButton.setOnAction(event -> handleCreateNode());
        connectNodesButton.setOnAction(event -> handleConnectNodes());
        executeWorkflowButton.setOnAction(event -> handleExecuteWorkflow());
        saveWorkflowButton.setOnAction(event -> handleSaveWorkflow());
        loadWorkflowButton.setOnAction(event -> handleLoadWorkflow());
        undoButton.setOnAction(event -> handleUndo());
        redoButton.setOnAction(event -> handleRedo());
        resetZoomButton.setOnAction(event -> handleResetZoom());
        zoomInButton.setOnAction(e -> handleZoomIn());
        zoomOutButton.setOnAction(e -> handleZoomOut());
        clearWorkspaceButton.setOnAction(event -> handleClearWorkspace());

        // === WORKSPACE TITLE LABEL ===
        // If a workflow file is already loaded, show its name in the workspace title
        if (currentWorkflowFile != null) {
            workspaceTitleField.setText("Workflow: " + currentWorkflowFile.getName());
        } else {
            workspaceTitleField.setText("Workflow: Untitled");
        }

        // === SIDEBAR COMBOBOX SETUP ===
        // Populate the ComboBox with all possible NodeTypes once
        sidebarNodeTypeCombo.getItems().setAll(NodeType.values());

        // Define how the NodeType enum is displayed in the ComboBox
        sidebarNodeTypeCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(NodeType nodeType) {
                return nodeType != null ? nodeType.name() : "";
            }

            @Override
            public NodeType fromString(String string) {
                try {
                    return NodeType.valueOf(string);
                } catch (IllegalArgumentException e) {
                    return null;
                }
            }
        });

        // === LIVE NODE TYPE UPDATER ===
        // When the selected value in the ComboBox changes, update the selected node's type and visual style
        sidebarNodeTypeCombo.setOnAction((ActionEvent comboEvent) -> {
            if (selectedSidebarNode != null) {
                NodeType selectedType = sidebarNodeTypeCombo.getValue();

                // Proceed only if the type actually changed
                if (selectedType != null && selectedType != selectedSidebarNode.getNodeType()) {
                    VBox nodeView = nodeViewsMap.get(selectedSidebarNode.getId());

                    if (nodeView != null) {

                        // Hide condition-specific UI elements if switching FROM a Condition node
                        if (selectedSidebarNode instanceof ConditionNode) {
                            TextField conditionField = (TextField) nodeView.lookup("#conditionExpressionField");
                            VBox branchBox = (VBox) nodeView.lookup("#conditionBranchBox");

                            if (conditionField != null) {
                                conditionField.setVisible(false);
                                conditionField.setManaged(false);
                            }
                            if (branchBox != null) {
                                branchBox.setVisible(false);
                                branchBox.setManaged(false);
                            }
                        }

                        // Update the node type label visually
                        Label typeLabel = (Label) nodeView.lookup("#nodeTypeLabel");
                        if (typeLabel != null) {
                            typeLabel.setText(selectedType.getDisplayName());

                            // Reset old style classes
                            nodeView.getStyleClass().removeIf(s -> s.endsWith("-node"));
                            typeLabel.getStyleClass().removeIf(s -> s.endsWith("-label"));

                            // Remove old prefix-based classes
                            String oldPrefix = selectedSidebarNode.getNodeType().getCssPrefix();
                            typeLabel.getStyleClass().remove(oldPrefix + "-label");
                            nodeView.getStyleClass().remove(oldPrefix + "-node");

                            // Update the actual node model with the new type
                            selectedSidebarNode.setNodeType(selectedType);

                            // Add new style classes based on selected type
                            String newPrefix = selectedType.getCssPrefix();
                            typeLabel.getStyleClass().add(newPrefix + "-label");
                            nodeView.getStyleClass().add(newPrefix + "-node");

                            // Visually color the ComboBox background based on node type
                            switch (selectedType) {
                                case TASK -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #4caf50;");
                                case CONDITION -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #ff9800;");
                                case PREDICTION -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #3f51b5;");
                                case ANALYSIS -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #9c27b0;");
                                case START, END -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #009688;");
                                case DATA -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #00bcd4;");
                                case OUTPUT -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #ff4081;");
                                case TRAINING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #ff5722;");
                                case VALIDATION -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #7cb342;");
                                case TESTING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #607d8b;");
                                case PREPROCESSING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #2196f3;");
                                case FEATURE_ENGINEERING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #ffb300;");
                                case MODEL_SELECTION -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #1a237e;");
                                case EVALUATION -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #4E342E;");
                                case INFERENCE -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #5e35b1;");
                                case CLUSTERING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #0097a7;");
                                case GNN_MODULE -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #673ab7;");
                                case ENSEMBLE -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #8bc34a;");
                                case MONITORING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #3e4a59;");
                                case EXPLAINABILITY -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #9e9d24;");
                                case HYPERPARAMETER_TUNING -> sidebarNodeTypeCombo.setStyle("-fx-background-color: #e91e63;");
                                default -> sidebarNodeTypeCombo.setStyle("");
                            }

                            // Show condition-specific UI if switching TO Condition type
                            if (selectedType == NodeType.CONDITION) {
                                TextField conditionField = (TextField) nodeView.lookup("#conditionExpressionField");
                                VBox branchBox = (VBox) nodeView.lookup("#conditionBranchBox");
                                Label yesLabel = (Label) nodeView.lookup("#yesTargetLabel");
                                Label noLabel = (Label) nodeView.lookup("#noTargetLabel");

                                if (conditionField != null) {
                                    conditionField.setVisible(true);
                                    conditionField.setManaged(true);
                                    if (selectedSidebarNode instanceof ConditionNode cNode)
                                        conditionField.setText(cNode.getConditionExpression());
                                }

                                if (branchBox != null) {
                                    branchBox.setVisible(true);
                                    branchBox.setManaged(true);
                                }

                                if (yesLabel != null && selectedSidebarNode instanceof ConditionNode cNode) {
                                    WorkflowNode yes = cNode.getYesTarget();
                                    yesLabel.setText(yes != null ? yes.getName() : "(Unconnected)");
                                }

                                if (noLabel != null && selectedSidebarNode instanceof ConditionNode cNode) {
                                    WorkflowNode no = cNode.getNoTarget();
                                    noLabel.setText(no != null ? no.getName() : "(Unconnected)");
                                }
                            }

                            // Apply style and layout changes to reflect immediately
                            nodeView.applyCss();
                            nodeView.layout();

                            System.out.println("✅ Node type changed and style updated to: " + selectedType);

                        } else {
                            System.err.println("❌ ERROR: nodeTypeLabel not found in node view");
                        }
                    } else {
                        System.err.println("❌ ERROR: nodeView not found for selected node");
                    }
                }
            }
        });





        // === Live Update for Node Name from Sidebar ===
// When the user presses Enter after editing the name in the sidebar text field,
// update both the node model and its visual label.
        sidebarNodeNameField.setOnAction(event -> {
            if (selectedSidebarNode != null) {
                String newName = sidebarNodeNameField.getText();
                selectedSidebarNode.setName(newName);

                VBox nodeView = nodeViewsMap.get(selectedSidebarNode.getId());
                if (nodeView != null) {
                    Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");
                    if (nameLabel != null) {
                        nameLabel.setText(newName);
                    }
                }

                System.out.println("Node name updated from sidebar: " + newName);

                // If the selected node is a ConditionNode, also refresh its specific UI elements
                if (selectedSidebarNode instanceof ConditionNode conditionNode && nodeView != null) {
                    TextField conditionField = (TextField) nodeView.lookup("#conditionExpressionField");
                    VBox branchBox = (VBox) nodeView.lookup("#conditionBranchBox");
                    Label yesLabel = (Label) nodeView.lookup("#yesTargetLabel");
                    Label noLabel = (Label) nodeView.lookup("#noTargetLabel");

                    if (conditionField != null) {
                        conditionField.setVisible(true);
                        conditionField.setManaged(true);
                        conditionField.setText(conditionNode.getConditionExpression());
                    }

                    if (branchBox != null) {
                        branchBox.setVisible(true);
                        branchBox.setManaged(true);
                    }

                    if (yesLabel != null) {
                        WorkflowNode yesTarget = conditionNode.getYesTarget();
                        yesLabel.setText(yesTarget != null ? yesTarget.getName() : "(Unconnected)");
                    }

                    if (noLabel != null) {
                        WorkflowNode noTarget = conditionNode.getNoTarget();
                        noLabel.setText(noTarget != null ? noTarget.getName() : "(Unconnected)");
                    }
                }
            }
        });

// === Canvas Setup and Zooming ===
// We create a large canvas grid to help visually align and place nodes.
// A light gray grid is drawn using vertical and horizontal lines.
        Group gridGroup = new Group();
        int gridSpacing = 20;
        int gridWidth = 5000;
        int gridHeight = 5000;

        for (int x = 0; x < gridWidth; x += gridSpacing) {
            Line vLine = new Line(x, 0, x, gridHeight);
            vLine.setStroke(Color.LIGHTGRAY);
            vLine.setOpacity(0.3);
            gridGroup.getChildren().add(vLine);
        }

        for (int y = 0; y < gridHeight; y += gridSpacing) {
            Line hLine = new Line(0, y, gridWidth, y);
            hLine.setStroke(Color.LIGHTGRAY);
            hLine.setOpacity(0.3);
            gridGroup.getChildren().add(hLine);
        }

// Set up the main scrollable workspace with zoom and pan support
        workspacePane.setPrefSize(gridWidth, gridHeight);
        workspacePane.setStyle("-fx-background-color: #f0f8ff;");
        contentGroup = new Group(gridGroup, workspacePane);
        scrollPane = new ScrollPane(contentGroup);
        scrollPane.setFitToWidth(false);
        scrollPane.setFitToHeight(false);
        scrollPane.setPannable(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);

// Title bar above the canvas with editable name field and buttons
        HBox titleBar = new HBox(10, workspaceTitleField, editTitleButton, saveTitleButton);
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPadding(new Insets(8));

        VBox centerWithTitle = new VBox(titleBar, scrollPane);
        rootPane.setCenter(centerWithTitle);

// Set initial zoom level for the canvas
        scaleValue = 0.6;
        contentGroup.setScaleX(scaleValue);
        contentGroup.setScaleY(scaleValue);

// Center the scrollbars after layout is applied
        Platform.runLater(() -> {
            scrollPane.setHvalue(scrollPane.getHmax() / 2);
            scrollPane.setVvalue(scrollPane.getVmax() / 2);
        });

// Enable zooming with Ctrl + Scroll wheel
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double delta = event.getDeltaY();
                scaleValue += delta > 0 ? zoomIntensity : -zoomIntensity;
                scaleValue = Math.max(0.3, Math.min(scaleValue, 1.5));
                contentGroup.setScaleX(scaleValue);
                contentGroup.setScaleY(scaleValue);
                event.consume();
            }
        });

// Enable panning by dragging with primary mouse button
        workspacePane.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown()) {
                isPanning = true;
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        workspacePane.setOnMouseDragged(event -> {
            if (isPanning) {
                double deltaX = event.getSceneX() - lastMouseX;
                double deltaY = event.getSceneY() - lastMouseY;
                scrollPane.setHvalue(scrollPane.getHvalue() - deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        workspacePane.setOnMouseReleased(event -> {
            isPanning = false;
        });

// === Multi-Node Selection Rectangle ===
// Draw a blue transparent rectangle to select multiple nodes by dragging
        selectionRectangle = new Rectangle();
        selectionRectangle.setStroke(Color.BLUE);
        selectionRectangle.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.3));
        selectionRectangle.setVisible(false);
        contentGroup.getChildren().add(selectionRectangle);

// When user starts dragging on the empty canvas, begin selection
        contentGroup.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && !connectModeActive && event.getTarget() instanceof Group) {
                rectStartX = event.getX();
                rectStartY = event.getY();
                selectionRectangle.setX(rectStartX);
                selectionRectangle.setY(rectStartY);
                selectionRectangle.setWidth(0);
                selectionRectangle.setHeight(0);
                selectionRectangle.setVisible(true);

                selectedNodes.forEach(node -> node.getStyleClass().remove("selected"));
                selectedNodes.clear();
            }
        });

// Resize the selection rectangle as the user drags the mouse
        contentGroup.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && !connectModeActive) {
                double width = event.getX() - rectStartX;
                double height = event.getY() - rectStartY;
                selectionRectangle.setWidth(Math.abs(width));
                selectionRectangle.setHeight(Math.abs(height));
                selectionRectangle.setX(Math.min(rectStartX, event.getX()));
                selectionRectangle.setY(Math.min(rectStartY, event.getY()));
            }
        });

// When mouse is released, finalize selection and hide the rectangle
        contentGroup.setOnMouseReleased(event -> {
            if (!connectModeActive) {
                selectNodesInsideRectangle();
                selectionRectangle.setVisible(false);
            }
        });

// Set up the title editing and saving buttons
        editTitleButton.setOnAction(e -> handleEditTitle());
        saveTitleButton.setOnAction(e -> handleRenameWorkflow());
    }

    /**
     * Handles the logic for creating a new node in the workflow when the "Create Node" button is clicked.
     * This method opens a dialog for the user to input node details, creates the node model,
     * initializes any custom fields based on the node type, adds the node visually to the workspace,
     * and registers the action in the undo/redo history.
     */
    @FXML
    private void handleCreateNode() {
        // Show the node creation dialog to collect user input
        NodeCreationDialog dialog = new NodeCreationDialog();
        Optional<NodeCreationDialog.NodeResult> result = dialog.showDialog();

        // Proceed only if the user confirmed creation
        result.ifPresent(nodeResult -> {
            if (mainController != null) {
                // Create the logical model of the node using the controller
                WorkflowNode newNode = mainController.createNode(
                        nodeResult.nodeType,
                        "node_" + System.currentTimeMillis(), // Auto-generated unique ID
                        nodeResult.nodeName
                );

                // Save the 'Details' field from the dialog into the node
                newNode.setDetails(nodeResult.details);

                // If the node type has a specific data field, assign the 'details' accordingly
                switch (nodeResult.nodeType) {
                    case TASK:
                        if (newNode instanceof TaskNode) {
                            ((TaskNode) newNode).setTaskDetails(nodeResult.details);
                        }
                        break;
                    case CONDITION:
                        if (newNode instanceof ConditionNode) {
                            ((ConditionNode) newNode).setConditionExpression(nodeResult.details);
                        }
                        break;
                    case PREDICTION:
                        if (newNode instanceof PredictionNode) {
                            ((PredictionNode) newNode).setModelName(nodeResult.details);
                        }
                        break;
                    case START:
                    case END:
                        // No specific field to assign for START or END nodes
                        break;
                }

                // Log node creation for debugging
                System.out.println("Created Node: " + newNode.getName() + ", ID: " + newNode.getId());

                // Add the node's visual representation to the workspace
                addNodeToWorkspace(newNode);
                System.out.println("Node created: " + newNode.getName());
                System.out.println("handleCreateNode() called in Controller: " + this);
                System.out.println("workspacePane instance: " + workspacePane);

                // Support undo/redo functionality by pushing the creation to the undo stack
                VBox nodeView = nodeViewsMap.get(newNode.getId());
                undoStack.push(new UndoableAction(
                        UndoableAction.ActionType.CREATE_NODE,
                        newNode,
                        0, 0, // Old position (irrelevant for creation)
                        nodeView.getLayoutX(), nodeView.getLayoutY() // New position
                ));

                // Clear redo stack because a new user action has occurred
                redoStack.clear();

                // Refresh the mini-map to reflect the new node
                updateMiniMap();
            } else {
                System.out.println("MainController is not set!");
            }

            // Optional: Bind the sidebar 'Details' text area to update the selected node's details
            sidebarDetailsArea.textProperty().addListener((obs, oldText, newText) -> {
                if (selectedSidebarNode != null) {
                    selectedSidebarNode.setDetails(newText);
                }
            });
        });
    }



    /**
     * Enables the mode for connecting two nodes.
     * Once activated, the user can click on two nodes to create a directional connection between them.
     */
    @FXML
    private void handleConnectNodes() {
        connectModeActive = true;  // Set flag to indicate connection mode is active
        firstSelectedNodeView = null;  // Clear any previous selections
        firstSelectedWorkflowNode = null;
        connectNodesButton.setStyle(connectModeActive ? "-fx-background-color: lightgreen;" : "");  // Visual cue
        System.out.println("Connect Mode activated! Click two nodes to connect.");
    }

    /**
     * Executes the workflow when the 'Execute Workflow' button is pressed.
     * The method starts execution from the defined start node(s), validates the workflow before execution,
     * and highlights the flow visually if validation passes.
     */
    @FXML
    private void handleExecuteWorkflow() {
        if (mainController != null) {
            visitedNodesGUI.clear();  // Clear previously executed nodes for a fresh run

            // Retrieve all start nodes in the workflow
            List<WorkflowNode> startNodes = mainController.getWorkflowService().findStartNodes();
            if (startNodes.isEmpty()) {
                System.out.println("No start node found. Cannot execute workflow.");
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Workflow Validation Failed");
                alert.setContentText("No start node found in the workflow.");
                alert.showAndWait();
                return;
            }

            WorkflowNode startNode = startNodes.get(0);  // Use the first start node found

            // Run validation before executing
            WorkflowValidationService validator = new WorkflowValidationService();
            boolean valid = validator.validateWorkflow(startNode, mainController.getWorkflowService().getAllNodes());

            if (valid) {
                // If the workflow is valid, execute each start node
                for (WorkflowNode node : startNodes) {
                    executeAndHighlightNode(node);
                }
                System.out.println("Workflow execution started!");
            } else {
                // Display validation error dialog to the user
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText("Workflow Validation Failed");
                alert.setContentText("Cannot execute workflow. Please fix validation errors first.");
                alert.showAndWait();
            }
        } else {
            System.out.println("MainController is not set!");
        }
    }

    /**
     * Generic method to execute a node that supports typed contextual execution using parametric polymorphism.
     *
     * @param node     the executable node instance
     * @param metadata a typed key-value map that provides context during execution
     */
    @SuppressWarnings("unchecked")
    private void executeGenericNode(ExecutableNode<?> node, Map<String, String> metadata) {
        if (node != null) {
            System.out.println("Generic Execution: " + node.getName());

            // Execute the node's default behavior
            node.execute();

            // Execute with additional context if supported
            node.executeWithContext(metadata);

            // Log the execution with metadata
            Map<String, String> logMap = new HashMap<>();
            logMap.put("result", "Executed node with metadata: " + metadata);

            GenericExecutionLogger<Map<String, String>> logger =
                    (GenericExecutionLogger<Map<String, String>>) node.getExecutionLogger();

            logger.log(logMap);
        }
    }





    // A set to track which nodes have already been executed and visually highlighted.
// Prevents infinite loops in cyclic workflows and ensures each node runs only once per execution.
    private final Set<String> visitedNodesGUI = new HashSet<>();

    /**
     * Executes a given workflow node and applies a visual animation (highlight).
     * Also handles branching logic, error reporting, live status updates, and
     * ensures downstream connected nodes are processed recursively.
     *
     * @param node the workflow node to execute and animate
     */
    private void executeAndHighlightNode(WorkflowNode node) {
        // Exit early if the node is null or has already been processed
        if (node == null || visitedNodesGUI.contains(node.getId())) return;
        visitedNodesGUI.add(node.getId());

        // Retrieve the corresponding visual VBox for the node
        VBox nodeView = nodeViewsMap.get(node.getId());
        if (nodeView == null) return;

        // If the node is selected in the sidebar, show temporary "Executing..." status
        if (selectedSidebarNode == node && sidebarExecutionStatusLabel != null) {
            sidebarExecutionStatusLabel.setText("Executing...");
            sidebarExecutionStatusLabel.setStyle("-fx-text-fill: orange;");
        }

        // Animate the node: flash yellow background briefly
        Timeline nodeTimeline = new Timeline(
                new KeyFrame(Duration.seconds(0),
                        e -> nodeView.setStyle(nodeView.getStyle() + "-fx-background-color: yellow;")),
                new KeyFrame(Duration.seconds(0.5),
                        e -> nodeView.setStyle(nodeView.getStyle().replace("-fx-background-color: yellow;", ""))
                )
        );
        nodeTimeline.setCycleCount(2);

        // Once animation finishes, execute the logic of the node
        nodeTimeline.setOnFinished(event -> {
            try {
                // Execute if it's an ExecutableNode (uses polymorphism)
                if (node instanceof ExecutableNode<?> executableNode) {
                    executeGenericNode(executableNode, executableNode.getMetadata());
                }

                // Update sidebar to show execution complete
                if (selectedSidebarNode == node && sidebarExecutionStatusLabel != null) {
                    sidebarExecutionStatusLabel.setText("Done");
                    sidebarExecutionStatusLabel.setStyle("-fx-text-fill: green;");
                }

                // Format a timestamped log message based on node type
                String timestamp = "[" + java.time.LocalTime.now().withNano(0) + "] ";
                String logMessage = switch (node.getNodeType()) {
                    case TASK -> "Task executed: " + node.getName();
                    case CONDITION -> "Condition evaluated: " + node.getName();
                    case PREDICTION -> "Model predicted result: " + node.getName();
                    case ANALYSIS -> "Analysis performed: " + node.getName();
                    case DATA -> "Data loaded: " + node.getName();
                    case OUTPUT -> "Output generated: " + node.getName();
                    case TRAINING -> "Model training started: " + node.getName();
                    case VALIDATION -> "Validation running: " + node.getName();
                    case TESTING -> "Testing completed: " + node.getName();
                    case PREPROCESSING -> "Preprocessing step executed: " + node.getName();
                    case FEATURE_ENGINEERING -> "Feature engineering done: " + node.getName();
                    case INFERENCE -> "Inference result produced: " + node.getName();
                    case MONITORING -> "Monitoring system update: " + node.getName();
                    case EXPLAINABILITY -> "Explainability applied to: " + node.getName();
                    case GNN_MODULE -> "GNN Module executed: " + node.getName();
                    case CLUSTERING -> "Clustering step applied: " + node.getName();
                    case ENSEMBLE -> "Ensemble model integrated: " + node.getName();
                    case MODEL_SELECTION -> "Model selection completed: " + node.getName();
                    case HYPERPARAMETER_TUNING -> "Hyperparameter tuning run: " + node.getName();
                    case START -> "Start node launched: " + node.getName();
                    case END -> "End node reached: " + node.getName();
                    default -> "Executed: " + node.getName();
                };

                // Append log message to terminal output and UI log box (if applicable)
                if (!(node.getType() == NodeType.START || node.getType() == NodeType.END ||
                        node.getType() == NodeType.DATA  || node.getType() == NodeType.OUTPUT)) {
                    System.out.println(timestamp + logMessage);
                    if (executionLogArea != null) {
                        executionLogArea.appendText(timestamp + logMessage + "\n");
                        executionLogArea.setScrollTop(Double.MAX_VALUE);
                    }
                }

            } catch (Exception ex) {
                // Handle execution error and report in UI
                String timestamp = "[" + java.time.LocalTime.now().withNano(0) + "] ";
                String errorLog = timestamp + "Error executing node: " + node.getName();
                System.err.println(errorLog);

                if (selectedSidebarNode == node && sidebarExecutionStatusLabel != null) {
                    sidebarExecutionStatusLabel.setText("Failed");
                    sidebarExecutionStatusLabel.setStyle("-fx-text-fill: red;");
                }

                if (executionLogArea != null) {
                    executionLogArea.appendText(errorLog + "\n");
                    executionLogArea.setScrollTop(Double.MAX_VALUE);
                }

                ex.printStackTrace();
            }

            // === Handle conditional logic nodes (ConditionNode) ===
            if (node instanceof ConditionNode conditionNode) {
                boolean result = conditionNode.evaluate();  // Run condition logic
                WorkflowNode target = result ? conditionNode.getYesTarget() : conditionNode.getNoTarget();

                // Fallback: infer targets if not explicitly set
                if (target == null) {
                    List<WorkflowConnection> outgoing = mainController.getWorkflowService().getConnectionsFrom(conditionNode);
                    if (outgoing.size() == 2) {
                        WorkflowConnection yesConn = outgoing.get(0);
                        WorkflowConnection noConn = outgoing.get(1);
                        target = result ? yesConn.getTargetNode() : noConn.getTargetNode();
                        if (result) conditionNode.setYesTarget(target);
                        else conditionNode.setNoTarget(target);
                    }
                }

                // Highlight and move to the next node via arrow
                Arrow arrow = connectionArrows.get(node.getId() + "->" + (target != null ? target.getId() : ""));
                if (arrow != null && target != null) {
                    highlightArrowThenContinue(arrow, target);
                } else {
                    System.out.println("Condition branch not connected: " + (result ? "YES" : "NO"));
                }

            } else {
                // For standard nodes, fetch and execute all connected target nodes
                List<WorkflowConnection> connections = mainController.getWorkflowService().getConnectionsFrom(node);
                if (connections != null) {
                    for (WorkflowConnection connection : connections) {
                        WorkflowNode targetNode = connection.getTargetNode();
                        Arrow arrow = connectionArrows.get(connection.getSourceNode().getId() + "->" + targetNode.getId());
                        if (arrow != null) {
                            highlightArrowThenContinue(arrow, targetNode);
                        } else if (!visitedNodesGUI.contains(targetNode.getId())) {
                            // Execute directly if no arrow visual is present
                            executeAndHighlightNode(targetNode);
                        }
                    }
                }
            }
        });

        nodeTimeline.play();  // Start animation + logic
    }



    /**
     * Handles the logic when a node is clicked during connection mode.
     * Implements a two-step selection: first click selects the source node,
     * and second click connects it to a target node after requesting a label.
     * <p>
     * Uses the Command pattern to record the connection operation
     * and supports undo/redo functionality.
     *
     * @param clickedNodeView the visual VBox of the node clicked
     * @param clickedWorkflowNode the underlying WorkflowNode data model
     */
    private void handleNodeClickForConnection(VBox clickedNodeView, WorkflowNode clickedWorkflowNode) {
        if (firstSelectedNodeView == null) {
            // Step 1: Select the first node (source)
            firstSelectedNodeView = clickedNodeView;
            firstSelectedWorkflowNode = clickedWorkflowNode;
            System.out.println("First node selected: " + clickedWorkflowNode.getName());
        } else {
            // Step 2: Select second node (target), then establish connection

            // Prompt user for an optional label for this connection
            TextInputDialog labelDialog = new TextInputDialog();
            labelDialog.setTitle("Connection Label");
            labelDialog.setHeaderText("Enter label for this connection:");
            Optional<String> labelResult = labelDialog.showAndWait();

            if (labelResult.isPresent()) {
                String label = labelResult.get();

                // Create and execute the connection command (Command pattern)
                ConnectNodesCommand connectCommand = new ConnectNodesCommand(
                        firstSelectedWorkflowNode,
                        clickedWorkflowNode,
                        mainController,
                        this
                );
                workflowInvoker.addCommand(connectCommand);
                workflowInvoker.runAll();

                // If a label was provided, apply it to the visual arrow
                if (!label.isEmpty()) {
                    String connectionKey = firstSelectedWorkflowNode.getId() + "->" + clickedWorkflowNode.getId();
                    Arrow arrow = connectionArrows.get(connectionKey);
                    if (arrow != null) {
                        arrow.setLabel(label);
                    }
                }

                // Add this action to the undo history
                undoStack.push(new UndoableAction(
                        UndoableAction.ActionType.CONNECT_NODES,
                        firstSelectedWorkflowNode,
                        clickedWorkflowNode
                ));
                redoStack.clear();

                System.out.println("Connection complete.");
            } else {
                // User cancelled the connection dialog
                System.out.println("Connection cancelled by user.");
            }

            // Always reset connection mode and button visuals after completion
            connectModeActive = false;
            connectNodesButton.setStyle("");
            firstSelectedNodeView = null;
            firstSelectedWorkflowNode = null;

            System.out.println("Connection complete. Exiting connect mode.");
        }
    }



    /**
     * Visually adds a WorkflowNode to the graphical workspace.
     * This method loads the FXML-based node template, assigns CSS styles
     * based on node type, places the node in the center of the current viewport,
     * and prepares type-specific UI elements (like condition expressions).
     *
     * @param node the logical WorkflowNode to be rendered on the workspace
     */
    private void addNodeToWorkspace(WorkflowNode node) {
        try {
            // Load the node's visual template from FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/farid/workfloworchestration/node-view.fxml"));
            VBox nodeView = loader.load();

            // Base node style
            nodeView.getStyleClass().add("node");

            // === Apply a unique CSS class depending on the node's functional type ===
            switch (node.getNodeType()) {
                case TASK -> nodeView.getStyleClass().add("task-node");
                case CONDITION -> nodeView.getStyleClass().add("condition-node");
                case PREDICTION -> nodeView.getStyleClass().add("prediction-node");
                case ANALYSIS -> nodeView.getStyleClass().add("analysis-node");
                case DATA -> nodeView.getStyleClass().add("data-node");
                case OUTPUT -> nodeView.getStyleClass().add("output-node");
                case START, END -> nodeView.getStyleClass().add("start-end-node");
                case TRAINING -> nodeView.getStyleClass().add("training-node");
                case VALIDATION -> nodeView.getStyleClass().add("validation-node");
                case TESTING -> nodeView.getStyleClass().add("testing-node");
                case PREPROCESSING -> nodeView.getStyleClass().add("preprocessing-node");
                case FEATURE_ENGINEERING -> nodeView.getStyleClass().add("feature-engineering-node");
                case MODEL_SELECTION -> nodeView.getStyleClass().add("model-selection-node");
                case EVALUATION -> nodeView.getStyleClass().add("evaluation-node");
                case INFERENCE -> nodeView.getStyleClass().add("inference-node");
                case CLUSTERING -> nodeView.getStyleClass().add("clustering-node");
                case GNN_MODULE -> nodeView.getStyleClass().add("gnn-module-node");
                case ENSEMBLE -> nodeView.getStyleClass().add("ensemble-node");
                case MONITORING -> nodeView.getStyleClass().add("monitoring-node");
                case EXPLAINABILITY -> nodeView.getStyleClass().add("explainability-node");
                case HYPERPARAMETER_TUNING -> nodeView.getStyleClass().add("hyperparameter-node");
            }

            // === Position the node at the center of the currently visible area ===
            double viewportX = scrollPane.getHvalue() * (scrollPane.getContent().getBoundsInLocal().getWidth() - scrollPane.getViewportBounds().getWidth());
            double viewportY = scrollPane.getVvalue() * (scrollPane.getContent().getBoundsInLocal().getHeight() - scrollPane.getViewportBounds().getHeight());

            double centerX = viewportX + scrollPane.getViewportBounds().getWidth() / 2;
            double centerY = viewportY + scrollPane.getViewportBounds().getHeight() / 2;

            // Offset slightly to avoid exact overlap if multiple nodes are added quickly
            double offset = Math.random() * 40 - 20;

            nodeView.setLayoutX(centerX - 75 + offset);
            nodeView.setLayoutY(centerY - 50 + offset);

            // === Bind UI elements from FXML to logic (for Condition nodes, etc.) ===
            Label typeLabel = (Label) nodeView.lookup("#nodeTypeLabel");
            Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");
            TextField conditionField = (TextField) nodeView.lookup("#conditionExpressionField");
            VBox conditionBranchBox = (VBox) nodeView.lookup("#conditionBranchBox");
            Label yesLabel = (Label) nodeView.lookup("#yesTargetLabel");
            Label noLabel = (Label) nodeView.lookup("#noTargetLabel");

            if (typeLabel != null) {
                typeLabel.setText(node.getNodeType().toString());

                switch (node.getNodeType()) {
                    case TASK -> typeLabel.getStyleClass().add("task-label");
                    case CONDITION -> typeLabel.getStyleClass().add("condition-label");
                    case PREDICTION -> typeLabel.getStyleClass().add("prediction-label");
                    case ANALYSIS -> typeLabel.getStyleClass().add("analysis-label");
                    case DATA -> typeLabel.getStyleClass().add("data-label");
                    case OUTPUT -> typeLabel.getStyleClass().add("output-label");
                    case START, END -> typeLabel.getStyleClass().add("start-end-label");
                    case TRAINING -> typeLabel.getStyleClass().add("training-label");
                    case VALIDATION -> typeLabel.getStyleClass().add("validation-label");
                    case TESTING -> typeLabel.getStyleClass().add("testing-label");
                    case PREPROCESSING -> typeLabel.getStyleClass().add("preprocessing-label");
                    case FEATURE_ENGINEERING -> typeLabel.getStyleClass().add("feature-engineering-label");
                    case MODEL_SELECTION -> typeLabel.getStyleClass().add("model-selection-label");
                    case EVALUATION -> typeLabel.getStyleClass().add("evaluation-label");
                    case INFERENCE -> typeLabel.getStyleClass().add("inference-label");
                    case CLUSTERING -> typeLabel.getStyleClass().add("clustering-label");
                    case GNN_MODULE -> typeLabel.getStyleClass().add("gnn-module-label");
                    case ENSEMBLE -> typeLabel.getStyleClass().add("ensemble-label");
                    case MONITORING -> typeLabel.getStyleClass().add("monitoring-label");
                    case EXPLAINABILITY -> typeLabel.getStyleClass().add("explainability-label");
                    case HYPERPARAMETER_TUNING -> typeLabel.getStyleClass().add("hyperparameter-label");
                }
            }

            if (nameLabel != null) {
                nameLabel.setText(node.getName());
            }

            // === Conditional logic node UI ===
            if (node.getNodeType() == NodeType.CONDITION) {
                if (conditionField != null) {
                    conditionField.setVisible(true);
                    conditionField.setManaged(true);
                    conditionField.setText("");
                    conditionField.textProperty().addListener((obs, oldVal, newVal) -> {
                        ((ConditionNode) node).setConditionExpression(newVal);
                    });
                }
                if (conditionBranchBox != null) {
                    conditionBranchBox.setVisible(true);
                    conditionBranchBox.setManaged(true);
                }
                if (yesLabel != null) yesLabel.setText("(Unconnected)");
                if (noLabel != null) noLabel.setText("(Unconnected)");
            } else {
                if (conditionField != null) {
                    conditionField.setVisible(false);
                    conditionField.setManaged(false);
                }
                if (conditionBranchBox != null) {
                    conditionBranchBox.setVisible(false);
                    conditionBranchBox.setManaged(false);
                }
            }
            // === Mouse click: select node or connect nodes ===
            nodeView.setOnMouseClicked(event -> {
                if (connectModeActive) {
                    handleNodeClickForConnection(nodeView, node);
                } else {
                    selectedSidebarNode = node;
                    updateSidebar(node);
                }
                event.consume();
            });
            // === Mouse drag: enable repositioning of nodes ===
            final Delta dragDelta = new Delta();
            nodeView.setOnMousePressed(event -> {
                dragDelta.x = event.getX();
                dragDelta.y = event.getY();
                event.consume();
            });


            nodeView.setOnMouseDragged(event -> {
                double newX = nodeView.getLayoutX() + event.getX() - dragDelta.x;
                double newY = nodeView.getLayoutY() + event.getY() - dragDelta.y;
                nodeView.setLayoutX(newX);
                nodeView.setLayoutY(newY);
                scrollPane.setPannable(false); // OK to keep this here to disable panning while dragging
                updateArrowsForNode(node);
                updateMiniMap();
                event.consume();
            });
            nodeView.setOnMouseReleased(event -> {
                scrollPane.setPannable(true); // Re-enable workspace drag after moving node
            });
            // === Final step: map ID and add to canvas ===
            nodeViewsMap.put(node.getId(), nodeView);
            workspacePane.getChildren().add(nodeView);
            System.out.println("✅ Node added with color class. ID: " + node.getId());

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "❌ Error loading node-view.fxml or adding node.", e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "❌ General error occurred while adding node.", e);
        }
    }

    /**
     * Getter for the sidebar ComboBox that allows users to change a node's type.
     * This ComboBox is typically located in the right-hand sidebar and used to update
     * the visual and functional type of the currently selected node.
     *
     * @return the ComboBox<NodeType> instance currently used in the sidebar
     */
    public ComboBox<NodeType> getSidebarNodeTypeCombo() {
        return sidebarNodeTypeCombo;
    }

    /**
     * Setter for the sidebar node type ComboBox.
     * Allows programmatic injection or modification of the sidebar’s node type selector,
     * useful during controller initialization or FXML binding.
     *
     * @param sidebarNodeTypeCombo the ComboBox<NodeType> to be assigned
     */
    public void setSidebarNodeTypeCombo(ComboBox<NodeType> sidebarNodeTypeCombo) {
        this.sidebarNodeTypeCombo = sidebarNodeTypeCombo;
    }

    public String getCurrentWorkflowFilename() {
        return currentWorkflowFilename;
    }

    public void setCurrentWorkflowFilename(String currentWorkflowFilename) {
        this.currentWorkflowFilename = currentWorkflowFilename;
    }

    /**
     * Inner helper class used to store delta values (x, y) during mouse press events.
     * This is primarily used for calculating node dragging offset, ensuring smooth
     * and relative repositioning when a node is clicked and dragged.
     */
    private static class Delta {
        double x, y;
    }



    /**
     * Updates the right-hand sidebar to reflect the details of the selected node.
     * This method is invoked when a user clicks on a node that is *not* in connect mode.
     * It synchronizes the sidebar fields (ID, name, type, details) with the selected node's data,
     * and makes the sidebar panel visible with live-reactive fields.
     *
     * @param node the WorkflowNode that was selected in the GUI
     */
    private void updateSidebar(WorkflowNode node) {
        // Set the node type ComboBox to match the selected node
        if (sidebarNodeTypeCombo != null) {
            sidebarNodeTypeCombo.setValue(node.getNodeType());

            //  Dynamically apply background color to match node type
            String sidebarColor = node.getNodeType().getSidebarColorHex();
            if (sidebarColor != null && !sidebarColor.isBlank()) {
                sidebarNodeTypeCombo.setStyle("-fx-background-color: " + sidebarColor + "; -fx-padding: 5px;");
            } else {
                sidebarNodeTypeCombo.setStyle(""); // Fallback style
            }
        }

        // === Update Sidebar Fields ===
        sidebarNodeId.setText(node.getId());  // Display node ID
        sidebarNodeNameField.setText(node.getName());  // Display current name

        //  Set details in multi-line TextArea (used for notes or logic)
        if (sidebarDetailsArea != null) {
            sidebarDetailsArea.setText(node.getDetails());
        }

        // Reset execution status to "Ready"
        if (sidebarExecutionStatusLabel != null) {
            sidebarExecutionStatusLabel.setText("Ready");
        }

        // Show the sidebar if it was hidden
        sidebar.setVisible(true);

        // === Reactively bind name field to update node + label in GUI ===
        sidebarNodeNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (selectedSidebarNode != null) {
                selectedSidebarNode.setName(newVal);  // Update model

                // Update label on visual node
                VBox nodeView = nodeViewsMap.get(selectedSidebarNode.getId());
                if (nodeView != null) {
                    Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");
                    if (nameLabel != null) {
                        nameLabel.setText(newVal);
                    }
                }
            }
        });

        // === Add contextual tooltips for usability ===
        Tooltip.install(sidebarNodeId, new Tooltip("Unique identifier for this node"));
        Tooltip.install(sidebarNodeTypeCombo, new Tooltip("Node type used to determine flow behavior"));
        Tooltip.install(sidebarNodeNameField, new Tooltip("Edit the node's display name"));
        Tooltip.install(sidebarDetailsArea, new Tooltip("Describe the node or add notes for logic"));

        // Final internal reference update
        selectedSidebarNode = node;
    }
}