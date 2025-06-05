package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

/**
 * StartNode
 *
 * Represents the entry point of a workflow.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link WorkflowNode}</li>
 *   <li><b>Polymorphism:</b> Overrides {@code execute()}, {@code isValid()}, {@code validateOperation()}</li>
 *   <li><b>Abstraction:</b> Defines generic behavior through base class and customizes it</li>
 * </ul>
 */
public class StartNode extends WorkflowNode {

    /**
     * Constructs a StartNode with a unique identifier and display name.
     *
     * @param id   Unique node ID
     * @param name Node label
     */
    public StartNode(String id, String name) {
        super(id, name, NodeType.START);
    }

    /**
     * Indicates that the start node is always valid.
     *
     * @return true (start nodes are always valid)
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the start node. This marks the beginning of a workflow.
     */
    @Override
    public void execute() {
        System.out.println("Start node execution begins.");
    }

    /**
     * Throws an exception if any specific operation is attempted.
     *
     * @param operation A label for an unsupported operation
     * @throws UnsupportedOperationForNodeException Always thrown
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
