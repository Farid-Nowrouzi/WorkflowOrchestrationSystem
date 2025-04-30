package com.farid.workfloworchestration.controller;

import com.farid.workfloworchestration.model.*;
import com.farid.workfloworchestration.view.NodeCreationDialog;
import com.farid.workfloworchestration.model.UndoableAction;
import com.farid.workfloworchestration.view.Arrow;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.ScrollEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Stack;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;




/**
 * MainViewController connects the GUI (main-view.fxml) with the application's logic.

 * OOP Concepts Implemented:
 * - Encapsulation: Fields are private and accessed through methods.
 * - Abstraction: GUI actions are abstracted from backend logic.
 * - Dependency Injection: MainController is injected into this class.
 * - High Cohesion: Focused only on handling user interactions.
 */
public class MainViewController {

    @FXML
    private Button createNodeButton;
    @FXML
    private Button connectNodesButton;
    @FXML
    private Button executeWorkflowButton;
    @FXML
    private Button saveWorkflowButton;
    @FXML
    private Button loadWorkflowButton;
    @FXML
    private Button clearWorkspaceButton;


    @FXML
    private Button resetZoomButton;
    @FXML
    private Button undoButton;
    @FXML
    private Button redoButton;


    @FXML
    private Pane miniMapPane;
    @FXML
    private Pane workspacePane;


    @FXML
    private VBox sidebar;
    @FXML
    private Label sidebarNodeId;
    @FXML
    private TextField sidebarNodeNameField;
    @FXML
    private Label sidebarNodeType;



    private MainController mainController; // Link to application logic (Injected)

    private Group workspaceGroup;
    private ScrollPane scrollPane;
    private double scaleValue = 1.0;
    private final double zoomIntensity = 0.05; // How fast zooming happens

    private double lastMouseX;
    private double lastMouseY;
    private boolean isPanning = false;

    private javafx.scene.shape.Rectangle selectionRectangle;
    private final List<VBox> selectedNodes = new ArrayList<>();
    private double rectStartX;
    private double rectStartY;

    private void selectNodesInsideRectangle() {
        for (VBox nodeView : nodeViewsMap.values()) {
            if (selectionRectangle.getBoundsInParent().intersects(nodeView.getBoundsInParent())) {
                selectedNodes.add(nodeView);
                nodeView.setStyle(nodeView.getStyle() + "-fx-border-color: blue; -fx-border-width: 2px;");
            }
        }
    }


    private final Map<String, VBox> nodeViewsMap = new HashMap<>();

    private final Map<String, Arrow> connectionArrows = new HashMap<>();

    private final Stack<UndoableAction> undoStack = new Stack<>();

    private final Stack<UndoableAction> redoStack = new Stack<>();

    private final List<Arrow> visualArrows = new ArrayList<>();


    @FXML
    private void handleAutoArrange() {
        autoArrangeNodes();
    }

    @FXML
    private void handleResetZoom() {
        scaleValue = 1.0;
        workspaceGroup.setScaleX(scaleValue);
        workspaceGroup.setScaleY(scaleValue);
        System.out.println("Zoom reset to 100%");
    }

    @FXML
    private void handleClearWorkspace() {
        javafx.scene.control.Alert confirmationDialog = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Clear Workspace");
        confirmationDialog.setHeaderText("Are you sure you want to clear the entire workspace?");
        confirmationDialog.setContentText("This action will remove all nodes and connections.");

        Optional<javafx.scene.control.ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            workspacePane.getChildren().clear();
            nodeViewsMap.clear();
            connectionArrows.clear();
            mainController.clearWorkflow();
            updateMiniMap();
            System.out.println("Workspace cleared successfully!");
        } else {
            System.out.println("Clear workspace canceled by user.");
        }
    }

    private WorkflowNode selectedSidebarNode;

    private boolean connectModeActive = false;

    private VBox firstSelectedNodeView = null;

    private WorkflowNode firstSelectedWorkflowNode = null;

    private static final Logger LOGGER = Logger.getLogger(MainViewController.class.getName());


    public void setSelectedSidebarNode(WorkflowNode node) {
        this.selectedSidebarNode = node;
    }


    private void highlightArrowThenContinue(Arrow arrow, WorkflowNode nextNode) {
        if (arrow == null || nextNode == null) return;

        javafx.animation.Timeline arrowTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0),
                        e -> arrow.getLine().setStroke(javafx.scene.paint.Color.LIGHTBLUE)),
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.5),
                        e -> arrow.getLine().setStroke(javafx.scene.paint.Color.BLACK))
        );
        arrowTimeline.setCycleCount(2); // Flash once

        arrowTimeline.setOnFinished(event -> {
            executeAndHighlightNode(nextNode);
        });

        arrowTimeline.play();
    }

    private void drawConnectionBetween(WorkflowNode source, WorkflowNode target) {
        if (source == null || target == null) return;

        VBox sourceView = nodeViewsMap.get(source.getId());
        VBox targetView = nodeViewsMap.get(target.getId());

        if (sourceView == null || targetView == null) return;

        double startX = sourceView.getLayoutX() + sourceView.getWidth() / 2;
        double startY = sourceView.getLayoutY() + sourceView.getHeight() / 2;
        double endX = targetView.getLayoutX() + targetView.getWidth() / 2;
        double endY = targetView.getLayoutY() + targetView.getHeight() / 2;

        Arrow arrow = new Arrow(startX, startY, endX, endY);
        visualArrows.add(arrow);
        workspacePane.getChildren().add(arrow);
    }



    /**
     * Detects cycles in the workflow graph.
     * Returns true if a cycle is detected, false otherwise.
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
        return false; // No cycles
    }

    /**
     * Helper method for cycle detection using DFS.
     */
    private boolean detectCycleUtil(WorkflowNode node, Map<String, Boolean> visited, Map<String, Boolean> recStack) {
        if (node == null) return false;

        String nodeId = node.getId();
        if (recStack.get(nodeId)) {
            return true; // Cycle detected
        }
        if (visited.get(nodeId)) {
            return false;
        }

        visited.put(nodeId, true);
        recStack.put(nodeId, true);

        List<WorkflowConnection> outgoingConnections = mainController.getWorkflowService().getConnectionsFrom(node);
        if (outgoingConnections != null) {
            for (WorkflowConnection connection : outgoingConnections) {
                WorkflowNode neighbor = connection.getTargetNode();
                if (detectCycleUtil(neighbor, visited, recStack)) {
                    return true;
                }
            }
        }

        recStack.put(nodeId, false);
        return false;
    }

    private boolean validateWorkflow() {
        boolean valid = true;
        StringBuilder errorMessages = new StringBuilder();

        // 🌟 First clear old red highlights
        for (VBox nodeView : nodeViewsMap.values()) {
            nodeView.setStyle(nodeView.getStyle().replace("-fx-border-color: red;", ""));
        }

        // Check 1: No disconnected nodes
        for (WorkflowNode node : mainController.getWorkflowService().getAllNodes()) {
            List<WorkflowConnection> outgoing = mainController.getWorkflowService().getConnectionsFrom(node);
            List<WorkflowConnection> incoming = mainController.getWorkflowService().getConnectionsTo(node);

            if ((outgoing == null || outgoing.isEmpty()) && (incoming == null || incoming.isEmpty())) {
                valid = false;
                errorMessages.append("Node \"").append(node.getName()).append("\" is not connected to anything.\n");

                VBox nodeView = nodeViewsMap.get(node.getId());
                if (nodeView != null) {
                    nodeView.setStyle(nodeView.getStyle() + "-fx-border-color: red; -fx-border-width: 3px;");
                }
            }
        }

        // Check 2: Detect cycles
        if (detectCycles()) {
            valid = false;
            errorMessages.append("Cycle detected in the workflow! Execution is not allowed.\n");
        }

        // 🌟 NEW Check 3: Node-type specific validation
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

        if (!valid) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Validation Error");
            alert.setHeaderText("Workflow validation failed!");
            alert.setContentText(errorMessages.toString());
            alert.showAndWait();
        }

        return valid;
    }

    @FXML
    private void handleUndo() {
        if (!undoStack.isEmpty()) {
            UndoableAction action = undoStack.pop();

            switch (action.getActionType()) {
                case CREATE_NODE:
                    // Remove the node
                    WorkflowNode nodeToRemove = action.getNode();
                    VBox nodeView = nodeViewsMap.get(nodeToRemove.getId());
                    if (nodeView != null) {
                        workspacePane.getChildren().remove(nodeView);
                        nodeViewsMap.remove(nodeToRemove.getId());
                    }
                    System.out.println("Undo: Node created removed - " + nodeToRemove.getName());
                    break;

                case DELETE_NODE:
                    // Re-add the node
                    WorkflowNode deletedNode = action.getNode();
                    addNodeToWorkspace(deletedNode);

                    // 🌟 NEW: Recreate the saved connections
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

                case MOVE_NODE:
                    // Move back to old position
                    WorkflowNode movedNode = action.getNode();
                    VBox movedNodeView = nodeViewsMap.get(movedNode.getId());
                    if (movedNodeView != null) {
                        movedNodeView.setLayoutX(action.getOldX());
                        movedNodeView.setLayoutY(action.getOldY());
                    }
                    System.out.println("Undo: Node moved back - " + movedNode.getName());
                    break;

                case DISCONNECT_NODES:
                    // Reconnect the nodes
                    WorkflowNode sourceNode = action.getSourceNode();
                    WorkflowNode targetNode = action.getTargetNode();
                    mainController.connectNodes(sourceNode, targetNode, null);
                    drawConnectionBetween(sourceNode, targetNode);
                    System.out.println("Undo: Connection restored between " + sourceNode.getName() + " and " + targetNode.getName());
                    break;

                default:
                    System.out.println("Undo: Unknown action type");
            }

            redoStack.push(action);
            updateMiniMap(); // Always update minimap after undo
        }
    }



    private void autoArrangeNodes() {
        int cols = 4; // Number of nodes per row
        int spacingX = 200;
        int spacingY = 150;
        int nodeWidth = 150;  // Approximate node width (you can adjust based on real node size)
        int nodeHeight = 100; // Approximate node height

        int totalNodes = nodeViewsMap.size();
        int rows = (int) Math.ceil((double) totalNodes / cols);

        double totalWidth = cols * spacingX;
        double totalHeight = rows * spacingY;

        double centerX = workspacePane.getWidth() / 2;
        double centerY = workspacePane.getHeight() / 2;

        double startX = centerX - (totalWidth / 2) + (spacingX - nodeWidth) / 2.0;
        double startY = centerY - (totalHeight / 2) + (spacingY - nodeHeight) / 2.0;


        int currentCol = 0;
        int currentRow = 0;

        javafx.animation.ParallelTransition parallelTransition = new javafx.animation.ParallelTransition();

        for (VBox nodeView : nodeViewsMap.values()) {
            double newX = startX + currentCol * spacingX;
            double newY = startY + currentRow * spacingY;

            javafx.animation.TranslateTransition transition = new javafx.animation.TranslateTransition(javafx.util.Duration.seconds(0.5), nodeView);
            transition.setToX(newX - nodeView.getLayoutX());
            transition.setToY(newY - nodeView.getLayoutY());

            parallelTransition.getChildren().add(transition);

            currentCol++;
            if (currentCol >= cols) {
                currentCol = 0;
                currentRow++;
            }
        }

        parallelTransition.setOnFinished(event -> {
            for (VBox nodeView : nodeViewsMap.values()) {
                double finalX = nodeView.getLayoutX() + nodeView.getTranslateX();
                double finalY = nodeView.getLayoutY() + nodeView.getTranslateY();
                nodeView.setLayoutX(finalX);
                nodeView.setLayoutY(finalY);
                nodeView.setTranslateX(0);
                nodeView.setTranslateY(0);
            }
            updateMiniMap(); // 🌟 Update minimap after moving!
        });

        parallelTransition.play();

        System.out.println("Auto-arranging nodes centered with smooth animation!");
    }



    private void updateMiniMap() {
        miniMapPane.getChildren().clear();

        double workspaceWidth = workspacePane.getWidth();
        double workspaceHeight = workspacePane.getHeight();

        double miniMapWidth = miniMapPane.getWidth();
        double miniMapHeight = miniMapPane.getHeight();

        double scaleX = miniMapWidth / workspaceWidth;
        double scaleY = miniMapHeight / workspaceHeight;
        double scale = Math.min(scaleX, scaleY);

        for (VBox nodeView : nodeViewsMap.values()) {
            javafx.scene.shape.Rectangle miniNode = new javafx.scene.shape.Rectangle();
            miniNode.setWidth(nodeView.getWidth() * scale);
            miniNode.setHeight(nodeView.getHeight() * scale);
            miniNode.setLayoutX(nodeView.getLayoutX() * scale);
            miniNode.setLayoutY(nodeView.getLayoutY() * scale);
            miniNode.setFill(javafx.scene.paint.Color.BLUE);
            miniMapPane.getChildren().add(miniNode);
        }
    }

    @FXML
    private void handleRedo() {
        if (!redoStack.isEmpty()) {
            UndoableAction action = redoStack.pop();

            switch (action.getActionType()) {
                case CREATE_NODE:
                    WorkflowNode nodeToReAdd = action.getNode();
                    addNodeToWorkspace(nodeToReAdd);
                    break;

                case DELETE_NODE:
                    // (later we'll fully implement)
                    break;

                case MOVE_NODE:
                    WorkflowNode movedNode = action.getNode();
                    VBox movedNodeView = nodeViewsMap.get(movedNode.getId());
                    if (movedNodeView != null) {
                        movedNodeView.setLayoutX(action.getNewX());
                        movedNodeView.setLayoutY(action.getNewY());
                    }
                    break;

                case CONNECT_NODES:
                    // Redo a connection (draw arrow again)
                    WorkflowNode source = action.getSourceNode();
                    WorkflowNode target = action.getTargetNode();

                    mainController.connectNodes(source, target, null);
                    drawConnectionBetween(source, target); // Use your existing method
                    System.out.println("Redo: Connection re-created between " + source.getName() + " and " + target.getName());
                    break;
            }

            undoStack.push(action);
            updateMiniMap();
        }
    }


    private void updateArrowRotation(Line line, javafx.scene.shape.Polygon arrowHead) {
        double ex = line.getEndX();
        double ey = line.getEndY();
        double sx = line.getStartX();
        double sy = line.getStartY();

        double angle = Math.atan2((ey - sy), (ex - sx)) * 180 / Math.PI;
        arrowHead.setRotate(angle);
    }

    @FXML
    private void handleSaveWorkflow() {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Save Workflow");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"));
            java.io.File file = fileChooser.showSaveDialog(workspacePane.getScene().getWindow());

            if (file != null) {
                saveWorkflowToFile(file);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while saving the workflow", e);

        }
    }

    private void saveWorkflowToFile(java.io.File file) {
        try {
            org.json.JSONObject workflowJson = new org.json.JSONObject();

            // Save Nodes
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

            // Save Connections
            org.json.JSONArray connectionsArray = new org.json.JSONArray();
            for (WorkflowConnection connection : mainController.getWorkflowService().getAllConnections()) {
                org.json.JSONObject connJson = new org.json.JSONObject();
                connJson.put("sourceId", connection.getSourceNode().getId());
                connJson.put("targetId", connection.getTargetNode().getId());

                // 🌟 NEW: Save the Arrow label if it exists
                String connectionKey = connection.getSourceNode().getId() + "->" + connection.getTargetNode().getId();
                if (connectionArrows.containsKey(connectionKey)) {
                    String label = connectionArrows.get(connectionKey).getLabel();
                    if (label != null && !label.isEmpty()) {
                        connJson.put("label", label);
                    }
                }

                connectionsArray.put(connJson);
            }

            workflowJson.put("nodes", nodesArray);
            workflowJson.put("connections", connectionsArray);


            java.nio.file.Files.writeString(file.toPath(), workflowJson.toString(4));
            System.out.println("Workflow saved successfully!");

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while saving the workflow", e);

        }
    }

    @FXML
    private void handleLoadWorkflow() {
        try {
            javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
            fileChooser.setTitle("Load Workflow");
            fileChooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("JSON Files", "*.json"));
            java.io.File file = fileChooser.showOpenDialog(workspacePane.getScene().getWindow());

            if (file != null) {
                loadWorkflowFromFile(file);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error occurred while loading the workflow", e);

        }
    }

    private void loadWorkflowFromFile(java.io.File file) {
        try {
            String content = java.nio.file.Files.readString(file.toPath());
            org.json.JSONObject workflowJson = new org.json.JSONObject(content);

            // Clear existing nodes
            workspacePane.getChildren().clear();
            nodeViewsMap.clear();
            mainController.clearWorkflow();

            // Load Nodes
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

            org.json.JSONArray connectionsArray = workflowJson.getJSONArray("connections");

            // Load Connections
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
                        // Create the Arrow visually
                        Arrow arrow = new Arrow(
                                sourceView.getLayoutX() + sourceView.getWidth() / 2,
                                sourceView.getLayoutY() + sourceView.getHeight() / 2,
                                targetView.getLayoutX() + targetView.getWidth() / 2,
                                targetView.getLayoutY() + targetView.getHeight() / 2
                        );

                        // 🌟 NEW: Load and set label if it exists
                        if (connJson.has("label")) {
                            arrow.setLabel(connJson.getString("label"));
                        }

                        // 🌟 IMPORTANT: Add arrow at index 0 (below nodes)
                        workspacePane.getChildren().add(0, arrow);

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


    private void editNode(WorkflowNode node, VBox nodeView) {
        javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog(node.getName());
        dialog.setTitle("Edit Node");
        dialog.setHeaderText("Editing Node: " + node.getName());
        dialog.setContentText("Enter new name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newName -> {
            node.setName(newName);

            // Update visual label
            Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");
            if (nameLabel != null) {
                nameLabel.setText(newName);
            }

            System.out.println("Node renamed to: " + newName);
        });
    }

    /**
     * Sets the MainController dependency (Dependency Injection).
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Initializes button actions after FXML is loaded.
     */
    @FXML
    private void initialize() {
        createNodeButton.setOnAction(event -> handleCreateNode());
        connectNodesButton.setOnAction(event -> handleConnectNodes());
        executeWorkflowButton.setOnAction(event -> handleExecuteWorkflow());
        saveWorkflowButton.setOnAction(event -> handleSaveWorkflow());
        loadWorkflowButton.setOnAction(event -> handleLoadWorkflow());
        undoButton.setOnAction(event -> handleUndo());
        redoButton.setOnAction(event -> handleRedo());
        resetZoomButton.setOnAction(event -> handleResetZoom());
        clearWorkspaceButton.setOnAction(event -> handleClearWorkspace());
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
            }
        });

        // Wrap workspacePane inside Group and ScrollPane for zoom support
        workspaceGroup = new Group();

        // 🌟 New: Create Grid Lines
        Group gridGroup = new Group();
        int gridSpacing = 20;
        int gridWidth = 5000; // Very large, you can adjust
        int gridHeight = 5000;

        for (int x = 0; x < gridWidth; x += gridSpacing) {
            Line vLine = new Line(x, 0, x, gridHeight);
            vLine.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
            vLine.setOpacity(0.3);
            gridGroup.getChildren().add(vLine);
        }
        for (int y = 0; y < gridHeight; y += gridSpacing) {
            Line hLine = new Line(0, y, gridWidth, y);
            hLine.setStroke(javafx.scene.paint.Color.LIGHTGRAY);
            hLine.setOpacity(0.3);
            gridGroup.getChildren().add(hLine);
        }

        workspaceGroup.getChildren().addAll(gridGroup, workspacePane);

        scrollPane = new ScrollPane(workspaceGroup);
        scrollPane.setPannable(true);

        // Replace workspacePane's parent with scrollPane
        if (workspacePane.getParent() instanceof Pane parentPane) {
            parentPane.getChildren().remove(workspacePane);
            parentPane.getChildren().add(scrollPane);
        }

        // Handle scroll zoom
        scrollPane.addEventFilter(ScrollEvent.SCROLL, event -> {
            if (event.isControlDown()) {
                double delta = event.getDeltaY();
                if (delta > 0) {
                    scaleValue += zoomIntensity;
                } else {
                    scaleValue -= zoomIntensity;
                }
                scaleValue = Math.max(0.2, Math.min(scaleValue, 3));

                workspaceGroup.setScaleX(scaleValue);
                workspaceGroup.setScaleY(scaleValue);
                event.consume();
            }
        });

        // Mouse events for panning and selection
        workspaceGroup.setOnMousePressed(event -> {
            if (event.isMiddleButtonDown()) {
                isPanning = true;
                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        workspaceGroup.setOnMouseDragged(event -> {
            if (isPanning) {
                double deltaX = event.getSceneX() - lastMouseX;
                double deltaY = event.getSceneY() - lastMouseY;

                scrollPane.setHvalue(scrollPane.getHvalue() - deltaX / scrollPane.getContent().getBoundsInLocal().getWidth());
                scrollPane.setVvalue(scrollPane.getVvalue() - deltaY / scrollPane.getContent().getBoundsInLocal().getHeight());

                lastMouseX = event.getSceneX();
                lastMouseY = event.getSceneY();
            }
        });

        workspaceGroup.setOnMouseReleased(event -> {
            isPanning = false;
        });

        // Initialize invisible selection rectangle
        selectionRectangle = new javafx.scene.shape.Rectangle();
        selectionRectangle.setStroke(javafx.scene.paint.Color.BLUE);
        selectionRectangle.setFill(javafx.scene.paint.Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.3));
        selectionRectangle.setVisible(false);

        workspaceGroup.getChildren().add(selectionRectangle);

        workspaceGroup.setOnMousePressed(event -> {
            if (event.isPrimaryButtonDown() && !connectModeActive) {
                rectStartX = event.getX();
                rectStartY = event.getY();
                selectionRectangle.setX(rectStartX);
                selectionRectangle.setY(rectStartY);
                selectionRectangle.setWidth(0);
                selectionRectangle.setHeight(0);
                selectionRectangle.setVisible(true);

                selectedNodes.forEach(node -> node.setStyle(""));
                selectedNodes.clear();
            }
        });

        workspaceGroup.setOnMouseDragged(event -> {
            if (event.isPrimaryButtonDown() && !connectModeActive) {
                double width = event.getX() - rectStartX;
                double height = event.getY() - rectStartY;
                selectionRectangle.setWidth(Math.abs(width));
                selectionRectangle.setHeight(Math.abs(height));
                selectionRectangle.setX(Math.min(rectStartX, event.getX()));
                selectionRectangle.setY(Math.min(rectStartY, event.getY()));
            }
        });

        workspaceGroup.setOnMouseReleased(event -> {
            if (!connectModeActive) {
                selectNodesInsideRectangle();
                selectionRectangle.setVisible(false);
            }
        });
    }


    /**
     * Handle creation of a node when 'Create Node' button is clicked.
     */
    @FXML
    private void handleCreateNode() {
        NodeCreationDialog dialog = new NodeCreationDialog();
        Optional<NodeCreationDialog.NodeResult> result = dialog.showDialog();

        result.ifPresent(nodeResult -> {
            if (mainController != null) {
                // Create the WorkflowNode logically
                WorkflowNode newNode = mainController.createNode(
                        nodeResult.nodeType,
                        "node_" + System.currentTimeMillis(), // Auto-generated ID
                        nodeResult.nodeName
                );

                // Set specific properties depending on node type
                switch (nodeResult.nodeType) {
                    case TASK:
                        if (newNode instanceof TaskNode) {
                            ((TaskNode) newNode).setTaskDetails(nodeResult.extraInfo);
                        }
                        break;
                    case CONDITION:
                        if (newNode instanceof ConditionNode) {
                            ((ConditionNode) newNode).setConditionExpression(nodeResult.extraInfo);
                        }
                        break;
                    case PREDICTION:
                        if (newNode instanceof PredictionNode) {
                            ((PredictionNode) newNode).setModelName(nodeResult.extraInfo);
                        }
                        break;
                }

                // Now visually add the node to the workspace
                addNodeToWorkspace(newNode);
                System.out.println("Node created: " + newNode.getName());

                // 🌟 Undo/Redo support
                // Undo/Redo support
                VBox nodeView = nodeViewsMap.get(newNode.getId());
                undoStack.push(new UndoableAction(
                        UndoableAction.ActionType.CREATE_NODE,
                        newNode,
                        0, 0,
                        nodeView.getLayoutX(), nodeView.getLayoutY()
                ));

                redoStack.clear(); // Clear redo stack because a new action happened
                updateMiniMap();


            } else {
                System.out.println("MainController is not set!");
            }
        });
    }


    /**
     * Handle connecting nodes when 'Connect Nodes' button is clicked.
     */
    @FXML
    private void handleConnectNodes() {
        connectModeActive = true;
        firstSelectedNodeView = null;
        firstSelectedWorkflowNode = null;
        connectNodesButton.setStyle(connectModeActive ? "-fx-background-color: lightgreen;" : "");
        System.out.println("Connect Mode activated! Click two nodes to connect.");
    }

    /**
     * Handle executing the workflow when 'Execute Workflow' button is clicked.
     */
    @FXML
    private void handleExecuteWorkflow() {
        if (mainController != null) {
            // 🌟 Validate the workflow before executing
            if (validateWorkflow()) {
                List<WorkflowNode> startNodes = mainController.getWorkflowService().findStartNodes();
                for (WorkflowNode node : startNodes) {
                    executeAndHighlightNode(node);
                }
                System.out.println("Workflow execution started!");
            } else {
                // 🌟 Show an error dialog
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


    private void executeAndHighlightNode(WorkflowNode node) {
        if (node == null) return;

        VBox nodeView = nodeViewsMap.get(node.getId());
        if (nodeView == null) return;

        javafx.animation.Timeline nodeTimeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0),
                        e -> nodeView.setStyle(nodeView.getStyle() + "-fx-background-color: yellow;")),
                new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.5),
                        e -> nodeView.setStyle(nodeView.getStyle().replace("-fx-background-color: yellow;", "")))
        );
        nodeTimeline.setCycleCount(2); // Flash once

        nodeTimeline.setOnFinished(event -> {
            node.execute(); // Logical execution after flash

            List<WorkflowConnection> connections = mainController.getWorkflowService().getConnectionsFrom(node);
            if (connections != null) {
                for (WorkflowConnection connection : connections) {
                    WorkflowNode targetNode = connection.getTargetNode();
                    Arrow arrow = connectionArrows.get(connection.getSourceNode().getId() + "->" + connection.getTargetNode().getId());
                    if (arrow != null) {
                        highlightArrowThenContinue(arrow, targetNode);
                    } else {
                        // If no arrow, just continue
                        executeAndHighlightNode(targetNode);
                    }
                }
            }
        });

        nodeTimeline.play();
    }



    private void handleNodeClickForConnection(VBox clickedNodeView, WorkflowNode clickedWorkflowNode) {
        if (firstSelectedNodeView == null) {
            // First node selected
            firstSelectedNodeView = clickedNodeView;
            firstSelectedWorkflowNode = clickedWorkflowNode;
            System.out.println("First node selected: " + clickedWorkflowNode.getName());
        } else {
            // Second node selected — now create connection
            WorkflowConnection connection = new WorkflowConnection(firstSelectedWorkflowNode, clickedWorkflowNode);

            // Tell MainController to register the connection
            mainController.connectNodes(firstSelectedWorkflowNode, clickedWorkflowNode, null);


            Arrow arrow = new Arrow(
                    firstSelectedNodeView.getLayoutX() + firstSelectedNodeView.getWidth() / 2,
                    firstSelectedNodeView.getLayoutY() + firstSelectedNodeView.getHeight() / 2,
                    clickedNodeView.getLayoutX() + clickedNodeView.getWidth() / 2,
                    clickedNodeView.getLayoutY() + clickedNodeView.getHeight() / 2
            );

// 🌟 Ask user for label
            TextInputDialog labelDialog = new TextInputDialog();
            labelDialog.setTitle("Connection Label");
            labelDialog.setHeaderText("Enter label for this connection:");
            Optional<String> labelResult = labelDialog.showAndWait();
            labelResult.ifPresent(arrow::setLabel);


// ✅ Add Arrow (Group of line + arrowhead + label) directly to workspace
            workspacePane.getChildren().add(0, arrow);

            // Save the arrow in the connectionArrows map
            String connectionKey = firstSelectedWorkflowNode.getId() + "->" + clickedWorkflowNode.getId();
            connectionArrows.put(connectionKey, arrow);

            System.out.println("Connected " + firstSelectedWorkflowNode.getName() + " -> " + clickedWorkflowNode.getName());

            undoStack.push(new UndoableAction(UndoableAction.ActionType.CONNECT_NODES, firstSelectedWorkflowNode, clickedWorkflowNode));
            redoStack.clear();


            // Reset connection mode
            connectModeActive = false;
            firstSelectedNodeView = null;
            firstSelectedWorkflowNode = null;
        }
    }

    /**
     * Helper method to visually add a node to the workspace.
     */
    private void addNodeToWorkspace(WorkflowNode node) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/farid/workfloworchestration/node-view.fxml"));
            VBox nodeView = loader.load();

            Label typeLabel = (Label) nodeView.lookup("#nodeTypeLabel");
            Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");

            typeLabel.setText(node.getNodeType().toString());
            nameLabel.setText(node.getName());

            // Random initial placement (temporary)
            nodeView.setLayoutX(Math.random() * 500);
            nodeView.setLayoutY(Math.random() * 300);

            // Register node view for future reference
            nodeViewsMap.put(node.getId(), nodeView);

            // 🌟 Multi-Select Support and Sidebar Update
            nodeView.setOnMouseClicked(event -> {
                if (connectModeActive) {
                    handleNodeClickForConnection(nodeView, node);
                } else {
                    if (event.isControlDown()) {
                        if (nodeView.getStyleClass().contains("selected")) {
                            nodeView.getStyleClass().remove("selected");
                        } else {
                            nodeView.getStyleClass().add("selected");
                        }
                    } else {
                        workspacePane.getChildren().filtered(child -> child instanceof VBox)
                                .forEach(child -> child.getStyleClass().remove("selected"));
                        nodeView.getStyleClass().add("selected");

                        updateSidebar(node);
                    }
                }
            });

            // Dragging behavior
            nodeView.setOnMousePressed(event -> {
                nodeView.setUserData(new double[]{event.getSceneX(), event.getSceneY(), nodeView.getLayoutX(), nodeView.getLayoutY()});
            });

            nodeView.setOnMouseDragged(event -> {
                double[] initialData = (double[]) nodeView.getUserData();
                double deltaX = event.getSceneX() - initialData[0];
                double deltaY = event.getSceneY() - initialData[1];

                workspacePane.getChildren().filtered(child -> child instanceof VBox && child.getStyleClass().contains("selected"))
                        .forEach(selectedNode -> {
                            double newX = ((VBox) selectedNode).getLayoutX() + deltaX;
                            double newY = ((VBox) selectedNode).getLayoutY() + deltaY;

                            int gridSize = 20;
                            newX = Math.round(newX / gridSize) * gridSize;
                            newY = Math.round(newY / gridSize) * gridSize;

                            ((VBox) selectedNode).setLayoutX(newX);
                            ((VBox) selectedNode).setLayoutY(newY);
                        });

                nodeView.setUserData(new double[]{event.getSceneX(), event.getSceneY(), nodeView.getLayoutX(), nodeView.getLayoutY()});
            });

            javafx.scene.control.ContextMenu contextMenu = new javafx.scene.control.ContextMenu();

            javafx.scene.control.MenuItem editItem = new javafx.scene.control.MenuItem("Edit Node Name");
            editItem.setOnAction(event -> {
                editNode(node, nodeView);
            });

            javafx.scene.control.MenuItem deleteItem = new javafx.scene.control.MenuItem("Delete Node");
            deleteItem.setOnAction(event -> {
                // 🌟 Confirmation dialog before deleting
                javafx.scene.control.Alert confirmAlert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Deletion");
                confirmAlert.setHeaderText("Delete Node: " + node.getName());
                confirmAlert.setContentText("Are you sure you want to delete this node and all its connections?");

                Optional<javafx.scene.control.ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    // 🌟 Save all connected connections BEFORE deletion
                    List<WorkflowConnection> connectionsToSave = mainController.getWorkflowService().getAllConnections()
                            .stream()
                            .filter(conn -> conn.getSourceNode().equals(node) || conn.getTargetNode().equals(node))
                            .toList();

                    // 1. Remove the node visual
                    workspacePane.getChildren().remove(nodeView);

                    // 2. Remove all arrows connected to this node
                    connectionArrows.entrySet().removeIf(entry -> {
                        String connectionKey = entry.getKey();
                        Arrow arrow = entry.getValue();
                        if (connectionKey.startsWith(node.getId() + "->") || connectionKey.endsWith("->" + node.getId())) {
                            workspacePane.getChildren().remove(arrow);
                            return true;
                        }
                        return false;
                    });

                    // 3. Remove from node map
                    nodeViewsMap.remove(node.getId());

                    // 🌟 Push DeleteNode undo action, including saved connections
                    undoStack.push(new UndoableAction(UndoableAction.ActionType.DELETE_NODE, node, connectionsToSave));
                    redoStack.clear(); // Clear redo stack
                    System.out.println("Node and its connections deleted: " + node.getName());

                    updateMiniMap();
                } else {
                    System.out.println("Deletion canceled for node: " + node.getName());
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem);

            nodeView.setOnContextMenuRequested(event -> {
                contextMenu.show(nodeView, event.getScreenX(), event.getScreenY());
            });

            nodeView.setOnMouseClicked(event -> {
                selectedSidebarNode = node;
                sidebarNodeNameField.setText(node.getName()); // optional: show name in sidebar
            });


            workspacePane.getChildren().add(nodeView);

        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occurred while adding to the workflow", e);

        }
    }

    /**
     * Returns the workspace pane where nodes will be visually added.
     */
    public Pane getWorkspacePane() {
        return workspacePane;
    }

    // Updated method: Enhancing labels with all 4 features + sidebar visibility
    private void updateSidebar(WorkflowNode node) {
        if (node == null) {
            sidebar.setVisible(false); // Hide sidebar if no node is selected
            return;
        }

        // 1. Show Full Node Details
        sidebarNodeId.setText(node.getId());
        sidebarNodeNameField.setText(node.getName());
        sidebarNodeType.setText(node.getNodeType().toString());

        // Make the sidebar visible
        sidebar.setVisible(true);

        // 2. Color Coding Based on Node Type
        switch (node.getNodeType()) {
            case TASK:
                sidebarNodeType.setStyle("-fx-background-color: lightblue; -fx-padding: 5px;");
                break;
            case CONDITION:
                sidebarNodeType.setStyle("-fx-background-color: lightyellow; -fx-padding: 5px;");
                break;
            case PREDICTION:
                sidebarNodeType.setStyle("-fx-background-color: lightpink; -fx-padding: 5px;");
                break;
            default:
                sidebarNodeType.setStyle(""); // Reset style if unknown
        }

        // 3. Real-Time Updates to Name Field
        sidebarNodeNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (selectedSidebarNode != null) {
                selectedSidebarNode.setName(newVal);
                VBox nodeView = nodeViewsMap.get(selectedSidebarNode.getId());
                if (nodeView != null) {
                    Label nameLabel = (Label) nodeView.lookup("#nodeNameLabel");
                    if (nameLabel != null) {
                        nameLabel.setText(newVal);
                    }
                }
            }
        });

        // 4. Add Hover Tooltips to Sidebar Labels/Field
        Tooltip.install(sidebarNodeId, new Tooltip("Unique identifier for this node"));
        Tooltip.install(sidebarNodeType, new Tooltip("Node type used to determine flow behavior"));
        Tooltip.install(sidebarNodeNameField, new Tooltip("Edit the node's display name"));

        // Final: Update selected node reference
        selectedSidebarNode = node;
    }



}

