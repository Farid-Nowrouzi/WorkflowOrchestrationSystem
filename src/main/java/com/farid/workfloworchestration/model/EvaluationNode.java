package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code EvaluationNode} represents a node in the workflow responsible
 * for evaluating a machine learning model using a specific metric
 * (e.g., accuracy, F1-score, precision).
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance</b>: Extends {@code ExecutableNode<String>}</li>
 *   <li><b>Polymorphism</b>: Overrides {@code execute()}, {@code isValid()}, {@code executeWithContext()}</li>
 *   <li><b>Encapsulation</b>: The evaluation metric is kept private and accessed via getters/setters</li>
 *   <li><b>Abstraction</b>: Hides complex evaluation behavior behind simple interface methods</li>
 *   <li><b>Generics</b>: Leverages {@code ExecutableNode<String>} to enable type-safe logging</li>
 * </ul>
 *
 * This class is part of the model package and is commonly used at the end of
 * training pipelines to validate model performance.
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class EvaluationNode extends ExecutableNode<String> {

    // === Encapsulated Field (Evaluation Strategy) ===
    private String evaluationMetric;

    // === Constructors (Overloaded - Ad-hoc Polymorphism) ===

    /**
     * Constructs an EvaluationNode with a default metric ("accuracy").
     */
    public EvaluationNode(String id, String name) {
        super(id, name, NodeType.EVALUATION);  // Inheritance + Enum usage
        this.evaluationMetric = "accuracy";    // Default value
    }

    /**
     * Constructs an EvaluationNode with a specific metric.
     */
    public EvaluationNode(String id, String name, String evaluationMetric) {
        super(id, name, NodeType.EVALUATION);
        this.evaluationMetric = evaluationMetric;
    }

    /**
     * Constructs an EvaluationNode with a description and metric.
     */
    public EvaluationNode(String id, String name, String description, String evaluationMetric) {
        super(id, name, description, NodeType.EVALUATION);
        this.evaluationMetric = evaluationMetric;
    }

    // === Encapsulation via Getters and Setters ===

    public String getEvaluationMetric() {
        return evaluationMetric;
    }

    public void setEvaluationMetric(String evaluationMetric) {
        this.evaluationMetric = evaluationMetric;
    }

    // === Overridden Execution Methods (Polymorphism) ===

    /**
     * Executes the evaluation logic without external context.
     */
    @Override
    public void execute() {
        System.out.println("📈 Evaluating model using metric: " + evaluationMetric +
                " in node: " + getName());
    }

    /**
     * Executes the evaluation logic with a given context.
     *
     * @param context Metadata or configuration that influences execution behavior
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("📈 Evaluation with context: " + context +
                " using metric: " + evaluationMetric);

        // ✅ Log the execution result using generic logger
        executionLogger.log("EvaluationNode executed with metric: " +
                evaluationMetric + " and context: " + context);
    }

    /**
     * Validates the node before execution.
     *
     * @return true if the evaluation metric is properly set
     */
    @Override
    public boolean isValid() {
        return evaluationMetric != null && !evaluationMetric.trim().isEmpty();
    }

    /**
     * Validates whether a certain operation is allowed on this node.
     *
     * @param operation The operation to validate
     * @throws UnsupportedOperationForNodeException Always thrown by default
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName());
    }
}
