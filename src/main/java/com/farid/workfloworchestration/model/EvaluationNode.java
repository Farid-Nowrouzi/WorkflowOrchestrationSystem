package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;
import com.farid.workfloworchestration.util.MetadataPrinter;

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

    // === Private Field (Information Hiding Compliance) ===
    private String evaluationMetric;

    // === Aggregation: Utility for printing metadata ===
    private final MetadataPrinter<Map<String, String>> metadataPrinter = new MetadataPrinter<>();

    // === Constructors (Overloaded - Ad-hoc Polymorphism) ===

    public EvaluationNode(String id, String name) {
        super(id, name, NodeType.EVALUATION);
        this.evaluationMetric = "accuracy";
    }

    public EvaluationNode(String id, String name, String evaluationMetric) {
        super(id, name, NodeType.EVALUATION);
        this.evaluationMetric = evaluationMetric;
    }

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

    @Override
    public void execute() {
        System.out.println(" Evaluating model using metric: " + evaluationMetric +
                " in node: " + getName());
    }

    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println(" Evaluation with context: " + context +
                " using metric: " + evaluationMetric);

        // === Demonstrate all unused overloads of MetadataPrinter ===
        System.out.println("[Step 1] printMetadata() no-args fallback:");
        metadataPrinter.printMetadata();

        System.out.println("[Step 2] printMetadata(context, prefix):");
        metadataPrinter.printMetadata(context, "[EVAL-META]");

        System.out.println("[Step 3] printMetadata(context, prefix, filter):");
        metadataPrinter.printMetadata(context, "[FILTERED]", "score");

        // === Log execution ===
        executionLogger.log("EvaluationNode executed with metric: " +
                evaluationMetric + " and context: " + context);
    }

    @Override
    public boolean isValid() {
        return evaluationMetric != null && !evaluationMetric.trim().isEmpty();
    }

    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName());
    }
}
