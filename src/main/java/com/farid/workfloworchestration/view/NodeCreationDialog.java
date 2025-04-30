package com.farid.workfloworchestration.view;

import com.farid.workfloworchestration.model.NodeType;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;
import javafx.util.Callback;

import java.util.Optional;

/**
 * Dialog for creating a new node (Task, Condition, Prediction).
 *
 * OOP Concepts:
 * - Encapsulation: UI and result model handled internally.
 * - Abstraction: Hides complexity from main controller.
 * - SRP: Focused only on node creation input.
 */
public class NodeCreationDialog extends Dialog<NodeCreationDialog.NodeResult> {

    private final ComboBox<NodeType> nodeTypeComboBox = new ComboBox<>();
    private final TextField nodeNameField = new TextField();
    private final TextField extraInfoField = new TextField();

    public NodeCreationDialog() {
        setTitle("Create New Node");
        setHeaderText("Enter details for the new node:");

        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Populate node types
        nodeTypeComboBox.getItems().addAll(NodeType.values());
        nodeTypeComboBox.setValue(NodeType.TASK);

        // Tooltips (optional but nice for user guidance)
        nodeTypeComboBox.setTooltip(new Tooltip("Choose the type of node (e.g., Task, Condition)"));
        nodeNameField.setTooltip(new Tooltip("Enter a unique node name"));
        extraInfoField.setTooltip(new Tooltip("Details: task info, condition, model name, etc."));

        // Add fields to layout
        grid.add(new Label("Node Type:"), 0, 0);
        grid.add(nodeTypeComboBox, 1, 0);
        grid.add(new Label("Node Name:"), 0, 1);
        grid.add(nodeNameField, 1, 1);
        grid.add(new Label("Details:"), 0, 2);
        grid.add(extraInfoField, 1, 2);

        getDialogPane().setContent(grid);

        // Disable OK button if name is empty
        Button createButton = (Button) getDialogPane().lookupButton(createButtonType);
        createButton.setDisable(true);  // Initially disabled

        nodeNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            createButton.setDisable(newVal.trim().isEmpty());
        });

        // Define how result is created
        setResultConverter(new Callback<ButtonType, NodeResult>() {
            @Override
            public NodeResult call(ButtonType dialogButton) {
                if (dialogButton == createButtonType) {
                    return new NodeResult(
                            nodeTypeComboBox.getValue(),
                            nodeNameField.getText().trim(),
                            extraInfoField.getText().trim()
                    );
                }
                return null;
            }
        });
    }

    /**
     * Shows the dialog and returns the result as an Optional.
     */
    public Optional<NodeResult> showDialog() {
        return showAndWait();
    }

    /**
     * Result class to return user's input.
     */
    public static class NodeResult {
        public final NodeType nodeType;
        public final String nodeName;
        public final String extraInfo;

        public NodeResult(NodeType nodeType, String nodeName, String extraInfo) {
            this.nodeType = nodeType;
            this.nodeName = nodeName;
            this.extraInfo = extraInfo;
        }
    }
}
