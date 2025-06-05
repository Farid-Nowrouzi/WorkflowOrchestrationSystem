package com.farid.workfloworchestration.factory;

import com.farid.workfloworchestration.exception.InvalidWorkflowException;
import com.farid.workfloworchestration.model.*;

/**
 * Factory class responsible for creating various types of {@link WorkflowNode} instances.
 *
 * <p>Implements the Factory Design Pattern to abstract and centralize node creation logic.
 * This promotes extensibility, consistency, and code reuse throughout the orchestration system.</p>
 *
 * <p>Supports overloaded creation methods for nodes with different levels of detail,
 * including validation and error signaling through exceptions.</p>
 *
 * <p>This class demonstrates multiple object-oriented principles, including:</p>
 * <ul>
 *     <li>Encapsulation: Internal construction logic is hidden</li>
 *     <li>Abstraction: Client code never deals with constructors directly</li>
 *     <li>Polymorphism: Ad-hoc through method overloading</li>
 *     <li>Loose Coupling: Code depends on high-level node types, not concrete classes</li>
 * </ul>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class NodeFactory {

    // === Factory Method: Create node with type, id, and name ===
    /**
     * Creates a node of the given type using a basic constructor.
     *
     * @param nodeType The type of node to create
     * @param id       The unique identifier for the node
     * @param name     The human-readable name of the node
     * @return A concrete WorkflowNode instance
     */
    public static WorkflowNode createNode(NodeType nodeType, String id, String name) { // Ad-hoc Polymorphism (overloading)
        return switch (nodeType) {
            // Abstraction: hides exact constructor details
            case TASK ->               new TaskNode(id, name);
            case CONDITION ->          new ConditionNode(id, name);
            case PREDICTION ->         new PredictionNode(id, name);
            case ANALYSIS ->           new AnalysisNode(id, name, "Statistical");
            case DATA ->               new DataNode(id, name);
            case OUTPUT ->             new OutputNode(id, name);
            case START ->              new StartNode(id, name);
            case END ->                new EndNode(id, name);
            case TRAINING ->           new TrainingNode(id, name);
            case VALIDATION ->         new ValidationNode(id, name);
            case TESTING ->            new TestingNode(id, name);
            case PREPROCESSING ->      new PreprocessingNode(id, name);
            case FEATURE_ENGINEERING -> new FeatureEngineeringNode(id, name);
            case EVALUATION ->         new EvaluationNode(id, name);
            case INFERENCE ->          new InferenceNode(id, name);
            case GNN_MODULE ->         new GNNmoduleNode(id, name);
            case CLUSTERING ->         new ClusteringNode(id, name);
            case ENSEMBLE ->           new EnsembleNode(id, name);
            case MONITORING ->         new MonitoringNode(id, name);
            case EXPLAINABILITY ->     new ExplainabilityNode(id, name);
            case HYPERPARAMETER_TUNING -> new HyperparameterTuningNode(id, name);
            case MODEL_SELECTION ->    new ModelSelectionNode(id, name);
            default -> throw new IllegalArgumentException("Invalid node type: " + nodeType);
        };
    }

    // === Overloaded Factory with Validation Support ===
    /**
     * Creates a node with extra metadata and validates it.
     *
     * @param nodeType  The type of node to create
     * @param id        The node's identifier
     * @param name      The node's label
     * @param extraInfo Additional metadata used in node construction
     * @return A validated WorkflowNode
     * @throws InvalidWorkflowException if validation fails
     */
    public static WorkflowNode createNode(NodeType nodeType, String id, String name, String extraInfo)
            throws InvalidWorkflowException {
        WorkflowNode node;

        // Ad-hoc Polymorphism: Overloaded factory method with extra parameter
        switch (nodeType) {
            case TASK ->               node = new TaskNode(id, name, extraInfo);
            case CONDITION ->          node = new ConditionNode(id, name, "Generated condition", extraInfo);
            case PREDICTION ->         node = new PredictionNode(id, name, "Generated prediction", extraInfo);
            case ANALYSIS ->           node = new AnalysisNode(id, name, extraInfo);
            case DATA ->               node = new DataNode(id, name);
            case OUTPUT ->             node = new OutputNode(id, name);
            case START ->              node = new StartNode(id, name);
            case END ->                node = new EndNode(id, name);
            case TRAINING ->           node = new TrainingNode(id, name, extraInfo);
            case VALIDATION ->         node = new ValidationNode(id, name, extraInfo);
            case TESTING ->            node = new TestingNode(id, name, extraInfo);
            case PREPROCESSING ->      node = new PreprocessingNode(id, name, extraInfo);
            case FEATURE_ENGINEERING -> node = new FeatureEngineeringNode(id, name, extraInfo);
            case EVALUATION ->         node = new EvaluationNode(id, name, extraInfo);
            case INFERENCE ->          node = new InferenceNode(id, name, extraInfo);
            case GNN_MODULE ->         node = new GNNmoduleNode(id, name, extraInfo);
            case CLUSTERING ->         node = new ClusteringNode(id, name, extraInfo);
            case ENSEMBLE ->           node = new EnsembleNode(id, name, extraInfo);
            case MONITORING ->         node = new MonitoringNode(id, name, extraInfo);
            case EXPLAINABILITY ->     node = new ExplainabilityNode(id, name, extraInfo);
            case HYPERPARAMETER_TUNING -> node = new HyperparameterTuningNode(id, name, extraInfo);
            case MODEL_SELECTION ->    node = new ModelSelectionNode(id, name, extraInfo);
            default -> throw new IllegalArgumentException("Invalid node type: " + nodeType);
        }

        // Encapsulation + Abstraction: Validation logic is internal to the node
        if (!node.isValid()) {
            // Information Hiding: Client doesn't deal with validation logic directly
            System.err.println("❌ Validation failed in NodeFactory for node: " + node.getName());
            throw new InvalidWorkflowException("Node failed validation: " + node.getName());
        }

        return node;
    }

    // === Convenience Factories with Auto-ID or Partial Parameters ===

    /**
     * Creates a node with an auto-generated ID and default name.
     *
     * @param nodeType The type of node
     * @return A WorkflowNode with generated values
     */
    public static WorkflowNode createNode(NodeType nodeType) {
        return createNode(nodeType, "AUTO_ID_" + System.currentTimeMillis(), "AUTO_NAME");
    }

    /**
     * Creates a node with a given ID and default name.
     *
     * @param nodeType The type of node
     * @param id       The ID to assign
     * @return A WorkflowNode with the given ID
     */
    public static WorkflowNode createNode(NodeType nodeType, String id) {
        return createNode(nodeType, id, "Unnamed Node");
    }
}
