package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

/**
 * DataNode represents a node in the workflow responsible for accessing or loading input data.
 *
 * <p>This node acts as a placeholder for data operations such as loading CSV files,
 * fetching data from a database, or preparing inputs for training or inference workflows.</p>
 *
 * <p>It inherits from {@link WorkflowNode} and demonstrates core OOP principles such as:</p>
 * <ul>
 *     <li><b>Inheritance</b>: Extends WorkflowNode</li>
 *     <li><b>Polymorphism</b>: Overrides {@code execute()}, {@code isValid()}, and {@code validateOperation()}</li>
 *     <li><b>Abstraction</b>: Hides internal execution details behind a simple method</li>
 *     <li><b>Encapsulation</b>: The logic is internally maintained and not exposed directly</li>
 * </ul>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class DataNode extends WorkflowNode {

    /**
     * Constructs a DataNode with a specific ID and name.
     *
     * @param id    Unique identifier for the node
     * @param name  Display name for the node
     */
    public DataNode(String id, String name) {
        super(id, name, NodeType.DATA); // ✅ Inheritance and constructor chaining
    }

    /**
     * Always returns true for now.
     * Validation logic can be added later (e.g., check if data is available).
     *
     * @return true if node is valid (default: always true)
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the node by simulating data access.
     * This method can be extended later to load files or connect to external sources.
     */
    @Override
    public void execute() {
        System.out.println("Executing Data Node: Loading or accessing data.");
    }

    /**
     * Validates a requested operation for the node.
     * Always throws an exception as DataNode does not support operations.
     *
     * @param operation The operation to validate
     * @throws UnsupportedOperationForNodeException Always thrown to reject operations
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
