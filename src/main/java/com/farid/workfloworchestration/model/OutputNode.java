package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

/**
 * OutputNode
 *
 * Represents a terminal node in the workflow responsible for producing or displaying output.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Extends {@link WorkflowNode}</li>
 *   <li><b>Abstraction:</b> Overrides abstract methods to define concrete execution behavior</li>
 *   <li><b>Polymorphism:</b> Custom behavior for {@code execute()} and {@code isValid()}</li>
 * </ul>
 */
public class OutputNode extends WorkflowNode {

    // === Information Hiding Compliance ===
// This class defines no new attributes.
// All fields are inherited from WorkflowNode and are already encapsulated.


    /**
     * Constructor that initializes the node with a unique ID and name.
     *
     * @param id   Unique identifier for the node
     * @param name Display name for the node
     */
    public OutputNode(String id, String name) {
        super(id, name, NodeType.OUTPUT);
    }

    /**
     * Determines whether this OutputNode is valid.
     * For now, always returns true, but can be extended to check for proper inputs.
     *
     * @return true if the node is valid for execution
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the node logic. In this case, represents outputting a result.
     * Can later be extended to display output, write to file, stream, or dashboard.
     */
    @Override
    public void execute() {
        System.out.println("Executing Output Node: Writing or displaying result.");
    }

    /**
     * Disallows unsupported operations for OutputNode by throwing a specific exception.
     *
     * @param operation The operation to validate
     * @throws UnsupportedOperationForNodeException if the operation is not permitted
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName());
    }
}
