package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * PredictionNode
 *
 * Represents a machine learning prediction step in the workflow.
 * This node uses a specified model to perform inference.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Extends {@link ExecutableNode}</li>
 *   <li><b>Polymorphism:</b> Overrides {@code execute()}, {@code isValid()}, and {@code executeWithContext()}</li>
 *   <li><b>Encapsulation:</b> The {@code modelName} field is private with getters/setters</li>
 *   <li><b>Parametric Polymorphism (Generics):</b> Inherits from {@code ExecutableNode<String>}</li>
 * </ul>
 */
public class PredictionNode extends ExecutableNode<String> {

    /**
     * Name of the machine learning model to be used for prediction.
     */
    private String modelName;

    /**
     * Constructor with only ID and name.
     * Initializes with an empty model name by default.
     *
     * @param id   Unique node identifier
     * @param name Display name
     */
    public PredictionNode(String id, String name) {
        super(id, name, NodeType.PREDICTION);
        this.modelName = "";
    }

    /**
     * Constructor that includes a description and model name.
     *
     * @param id         Node identifier
     * @param name       Node display name
     * @param description Human-readable description of this node
     * @param modelName   Name of the ML model to use
     */
    public PredictionNode(String id, String name, String description, String modelName) {
        super(id, name, description, NodeType.PREDICTION);
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    /**
     * Executes the prediction using the configured model.
     */
    @Override
    public void execute() {
        System.out.println("Running prediction using model: " + modelName + " for node: " + getName());
    }

    /**
     * Verifies if the prediction node has a valid model name.
     *
     * @return true if model name is not null or empty
     */
    @Override
    public boolean isValid() {
        return modelName != null && !modelName.trim().isEmpty();
    }

    /**
     * Performs prediction using the given execution context.
     *
     * @param context Runtime metadata (e.g., features, parameters)
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("Running prediction with context: " + context + " for node: " + getName());
        executionLogger.log("PredictionNode executed with model: " + modelName + " and context: " + context);
    }

    /**
     * Throws exception for unsupported operations.
     *
     * @param operation Operation name
     * @throws UnsupportedOperationForNodeException Always thrown for this node
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
