package com.farid.workfloworchestration.model;

// === Information Hiding Compliance ===
// All fields are declared private and final as required.
// Enum instances encapsulate constant metadata for each node type.


/**
 * {@code NodeType} is an enumeration that defines all possible node types
 * in the Machine Learning Workflow Orchestration System.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Abstraction:</b> Each type represents an abstract behavior in the workflow</li>
 *   <li><b>Enum as Class:</b> Uses fields, constructor, and methods like a full class</li>
 *   <li><b>Encapsulation:</b> Internal data (displayName, cssPrefix) hidden via accessors</li>
 * </ul>
 *
 * <p>Each node type includes:</p>
 * <ul>
 *   <li>{@code displayName}: Human-readable label for UI panels</li>
 *   <li>{@code cssPrefix}: CSS class prefix for dynamic styling</li>
 *   <li>{@code getSidebarColorHex()}: Background color used in the sidebar</li>
 * </ul>
 */
public enum NodeType {

    //  General-purpose workflow types
    TASK("Task", "task"),
    CONDITION("Condition", "condition"),
    PREDICTION("Prediction", "prediction"),
    ANALYSIS("Analysis", "analysis"),
    START("Start", "start-end"),
    END("End", "start-end"),
    DATA("Data", "data"),
    OUTPUT("Output", "output"),

    //  Machine learning workflow types
    TRAINING("Training", "training"),
    VALIDATION("Validation", "validation"),
    TESTING("Testing", "testing"),
    PREPROCESSING("Preprocessing", "preprocessing"),
    FEATURE_ENGINEERING("Feature Engineering", "feature-engineering"),
    MODEL_SELECTION("Model Selection", "model-selection"),
    EVALUATION("Evaluation", "evaluation"),
    INFERENCE("Inference", "inference"),
    CLUSTERING("Clustering", "clustering"),
    GNN_MODULE("GNN Module", "gnn-module"),
    ENSEMBLE("Ensemble", "ensemble"),
    MONITORING("Monitoring", "monitoring"),
    EXPLAINABILITY("Explainability", "explainability"),
    HYPERPARAMETER_TUNING("Hyperparameter Tuning", "hyperparameter");

    private final String displayName;
    private final String cssPrefix;

    NodeType(String displayName, String cssPrefix) {
        this.displayName = displayName;
        this.cssPrefix = cssPrefix;
    }

    /**
     * Returns the display name used in UI panels and tooltips.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the CSS prefix used to map visual styles dynamically.
     */
    public String getCssPrefix() {
        return cssPrefix;
    }

    /**
     * Returns the background color used for sidebar selection.
     * This method aids in UI consistency and type-based visual distinction.
     */
    public String getSidebarColorHex() {
        return switch (this) {
            case TASK -> "#e3fce3";
            case CONDITION -> "#fff8e1";
            case PREDICTION -> "#e8eaf6";
            case ANALYSIS -> "#f3e5f5";
            case START, END -> "#e0f2f1";
            case DATA -> "#e0f7fa";
            case OUTPUT -> "#fce4ec";
            case TRAINING -> "#ffe0b2";
            case VALIDATION -> "#f1f8e9";
            case TESTING -> "#cfd8dc";
            case PREPROCESSING -> "#e3f2fd";
            case FEATURE_ENGINEERING -> "#fff8e1";
            case MODEL_SELECTION -> "#e8eaf6";
            case EVALUATION -> "#efebe9";
            case INFERENCE -> "#ede7f6";
            case CLUSTERING -> "#e0f7fa";
            case GNN_MODULE -> "#ede7f6";
            case ENSEMBLE -> "#f1f8e9";
            case MONITORING -> "#cfd8dc";
            case EXPLAINABILITY -> "#f9fbe7";
            case HYPERPARAMETER_TUNING -> "#fce4ec";
        };
    }

    /**
     * Custom string representation for dropdowns and logs.
     */
    @Override
    public String toString() {
        return displayName;
    }
}
