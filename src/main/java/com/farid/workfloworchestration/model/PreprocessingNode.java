package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;
import java.util.Map;

/**
 * PreprocessingNode
 *
 * Represents a node in the workflow responsible for preprocessing datasets
 * before they are used by downstream ML components.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link ExecutableNode}</li>
 *   <li><b>Polymorphism:</b> Overrides {@code execute()}, {@code executeWithContext()}, {@code isValid()}</li>
 *   <li><b>Encapsulation:</b> Private {@code preprocessingSteps} field with accessors</li>
 *   <li><b>Parametric Polymorphism (Generics):</b> Uses {@code ExecutableNode<String>}</li>
 * </ul>
 */
public class PreprocessingNode extends ExecutableNode<String> {

    /**
     * Defines the preprocessing strategy to apply (e.g., normalization, scaling).
     */
    private String preprocessingSteps;

    /**
     * Basic constructor with default preprocessing steps.
     *
     * @param id   Unique node identifier
     * @param name Node display name
     */
    public PreprocessingNode(String id, String name) {
        super(id, name, NodeType.PREPROCESSING);
        this.preprocessingSteps = "default";  // Default preprocessing logic
    }

    /**
     * Constructor with specified preprocessing steps.
     *
     * @param id    Node identifier
     * @param name  Display name
     * @param steps Preprocessing logic description
     */
    public PreprocessingNode(String id, String name, String steps) {
        super(id, name, "Preprocessing logic: " + steps, NodeType.PREPROCESSING);
        this.preprocessingSteps = steps;
    }

    public String getPreprocessingSteps() {
        return preprocessingSteps;
    }

    public void setPreprocessingSteps(String preprocessingSteps) {
        this.preprocessingSteps = preprocessingSteps;
    }

    /**
     * Default execution method with no context (delegates to {@code executeWithContext}).
     */
    @Override
    public void execute() {
        executeWithContext(null);
    }

    /**
     * Executes the node using a metadata context map.
     *
     * @param context Metadata containing dataset information (optional)
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        String dataset = context != null && context.containsKey("dataset")
                ? context.get("dataset")
                : "Unknown Dataset";

        String result = "🧹 Preprocessed: " + dataset + " using steps: " + preprocessingSteps;

        // Log result using the generic logger
        executionLogger.log(result);

        // Output to console
        System.out.println(result);
    }

    /**
     * Validates if the preprocessing configuration is usable.
     *
     * @return true if the preprocessing steps are set
     */
    @Override
    public boolean isValid() {
        return preprocessingSteps != null && !preprocessingSteps.isBlank();
    }

    /**
     * By default, throws an exception if operations are attempted.
     *
     * @param operation Operation label
     * @throws UnsupportedOperationForNodeException Always thrown in this node
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
