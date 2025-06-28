package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code GNNmoduleNode} represents a node in the workflow that performs Graph Neural Network
 * operations—such as message passing, aggregation, or node embeddings—within a larger ML pipeline.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link ExecutableNode}</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li>Runtime polymorphism: Overrides abstract methods</li>
 *       <li>Parametric polymorphism: Uses generic type parameter {@code <String>}</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> GNN execution logic and logging are modularized and hidden</li>
 *   <li><b>Abstraction:</b> Provides specific logic for generic abstract methods</li>
 * </ul>
 *
 * <p>This node can be used in dynamic, modular AI pipelines where graph-structured data
 * is processed (e.g., molecules, social networks, knowledge graphs).</p>
 */
public class GNNmoduleNode extends ExecutableNode<String> {

    // === Information Hiding Compliance ===
// This class defines no new attributes.
// All encapsulated fields are inherited from ExecutableNode<String>.


    /**
     * Basic constructor without description.
     *
     * @param id   Unique identifier
     * @param name Display name
     */
    public GNNmoduleNode(String id, String name) {
        super(id, name, NodeType.GNN_MODULE);
    }

    /**
     * Constructor with a descriptive label for UI or documentation.
     *
     * @param id          Node ID
     * @param name        Node display name
     * @param description A description of what this GNN module does
     */
    public GNNmoduleNode(String id, String name, String description) {
        super(id, name, description, NodeType.GNN_MODULE);
    }

    /**
     * Default execution logic without dynamic parameters.
     */
    @Override
    public void execute() {
        System.out.println(" GNN Module node executed: running Graph Neural Network logic...");
    }

    /**
     * Checks if the node is correctly initialized.
     *
     * @return {@code true} (extendable if validation of model config is needed)
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Context-aware execution with runtime metadata (e.g., graph type, input shape).
     *
     * @param context Metadata passed from controller or orchestrator
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println(" GNN Module node executed with context: " + context);

        //  Log execution
        executionLogger.log("GNNmoduleNode executed with context: " + context);
    }

    /**
     * Prevents unsupported custom operations from being applied.
     *
     * @param operation The operation being validated
     * @throws UnsupportedOperationForNodeException Always thrown for this node type
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName()
        );
    }
}
