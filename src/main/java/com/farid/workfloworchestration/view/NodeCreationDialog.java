package com.farid.workfloworchestration.view;

import com.farid.workfloworchestration.model.NodeType;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.util.Optional;

/**
 * NodeCreationDialog
 *
 * <p>This class defines a reusable dialog component in JavaFX that prompts the user
 * to create a new node by entering its type, name, and optional details.</p>
 *
 * <p><strong>OOP Principles Applied:</strong></p>
 * <ul>
 *   <li><b>Encapsulation:</b> All internal logic and layout are hidden from the caller.</li>
 *   <li><b>Abstraction:</b> Provides a clean, high-level interface (`showDialog()`).</li>
 *   <li><b>Single Responsibility Principle (SRP):</b> Focuses solely on node creation UI.</li>
 * </ul>
 */
public class NodeCreationDialog extends Dialog<NodeCreationDialog.NodeResult> {

    // === Form Fields ===
    private final ComboBox<NodeType> nodeTypeComboBox = new ComboBox<>();
    private final TextField nodeNameField = new TextField();
    private final TextArea detailsArea = new TextArea(); // ✅ Multiline input for rich node description

    /**
     * Constructor – Initializes the dialog layout, input fields, and validation logic.
     */
    public NodeCreationDialog() {
        // === Global Dialog Settings ===
        setTitle("Create New Node");
        setHeaderText("Enter details for the new node:");
        getDialogPane().getStylesheets().add(
                getClass().getResource("/com/farid/workfloworchestration/style.css").toExternalForm()
        );

        // === Define Dialog Buttons ===
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // === Layout Setup ===
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(20));
        grid.setHgap(10);
        grid.setVgap(14);
        grid.setPrefWidth(400);
        grid.getStyleClass().add("node-dialog");

        // === Column Sizing ===
        ColumnConstraints col1 = new ColumnConstraints(); // Label column
        col1.setMinWidth(100);

        ColumnConstraints col2 = new ColumnConstraints(); // Input field column
        col2.setHgrow(Priority.ALWAYS);
        col2.setPrefWidth(250);

        grid.getColumnConstraints().addAll(col1, col2);

        // === Populate Node Types ===
        nodeTypeComboBox.getItems().addAll(NodeType.values());
        nodeTypeComboBox.setValue(NodeType.TASK); // Default selection

        // === Input Field Tooltips ===
        nodeTypeComboBox.setTooltip(new Tooltip("Choose the type of node (e.g., Task, Condition)"));
        nodeNameField.setTooltip(new Tooltip("Enter a unique node name"));
        detailsArea.setTooltip(new Tooltip("Optional notes, task logic, model, or condition..."));

        // === Node Name Field Styling ===
        nodeNameField.setPromptText("Enter full node name here...");
        nodeNameField.setPrefWidth(260);
        nodeNameField.setPrefColumnCount(20);
        nodeNameField.setStyle("-fx-font-size: 13px;");
        nodeNameField.getStyleClass().add("node-dialog-input");

        // === Details Area Setup ===
        detailsArea.setPromptText("Node-specific notes or logic...");
        detailsArea.setPrefRowCount(3);
        detailsArea.setWrapText(true);
        detailsArea.setMaxHeight(80);
        detailsArea.getStyleClass().add("node-dialog-textarea");

        // === Labels ===
        Label typeLabel = new Label("Node Type:");
        typeLabel.getStyleClass().add("node-dialog-label");

        Label nameLabel = new Label("Node Name:");
        nameLabel.getStyleClass().add("node-dialog-label");

        Label detailsLabel = new Label("Details:");
        detailsLabel.getStyleClass().add("node-dialog-label");

        // === Add All Components to Grid ===
        grid.add(typeLabel, 0, 0);
        grid.add(nodeTypeComboBox, 1, 0);

        grid.add(nameLabel, 0, 1);
        grid.add(nodeNameField, 1, 1);
        GridPane.setColumnSpan(nodeNameField, 2); // Allows wider field

        grid.add(detailsLabel, 0, 2);
        grid.add(detailsArea, 1, 2);

        getDialogPane().setContent(grid);

        // === Button Activation Logic ===
        Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);
        nodeNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            createButton.setDisable(newVal.trim().isEmpty());
        });

        // === Handle User Submission ===
        setResultConverter(new Callback<ButtonType, NodeResult>() {
            @Override
            public NodeResult call(ButtonType dialogButton) {
                if (dialogButton == createButtonType) {
                    NodeResult result = new NodeResult();
                    result.nodeType = nodeTypeComboBox.getValue();
                    result.nodeName = nodeNameField.getText().trim();
                    result.details = detailsArea.getText().trim();
                    return result;
                }
                return null;
            }
        });
    }

    /**
     * Opens the dialog and returns an Optional result (if user clicked 'Create').
     */
    public Optional<NodeResult> showDialog() {
        return showAndWait();
    }

    /**
     * NodeResult — Simple data holder for dialog output.
     */
    public static class NodeResult {
        public NodeType nodeType;
        public String nodeName;
        public String details; // ✅ Custom notes or logic attached to the node
    }
}
