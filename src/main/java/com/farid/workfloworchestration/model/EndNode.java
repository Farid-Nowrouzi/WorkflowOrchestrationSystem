package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

/**
 * {@code EndNode} represents the terminal point in a workflow.
 * No further computation or logic occurs beyond this node.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance</b>: Inherits behavior and structure from {@code WorkflowNode}</li>
 *   <li><b>Polymorphism (Override)</b>: Customizes {@code execute()}, {@code isValid()}, and {@code validateOperation()}</li>
 *   <li><b>Abstraction</b>: Hides internal workflow mechanics while exposing simple execution semantics</li>
 *   <li><b>Encapsulation</b>: Internal validation and behavior remain hidden from external consumers</li>
 * </ul>
 *
 * <p>End nodes usually mark the completion of a machine learning or data processing workflow.
 * This class ensures semantic clarity while conforming to the unified node structure.</p>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class EndNode extends WorkflowNode {

    /**
     * Constructs an {@code EndNode} with a unique identifier and label.
     *
     * @param id    The unique identifier of the node.
     * @param name  The display name of the node.
     */
    public EndNode(String id, String name) {
        super(id, name, NodeType.END); // Inheritance + Enum usage
    }

    /**
     * {@inheritDoc}
     * For end nodes, validation is trivially true by default.
     */
    @Override
    public boolean isValid() {
        return true; // Always valid unless extended with extra logic
    }

    /**
     * Executes the node.
     * <p>For EndNode, this signifies terminal workflow state. No further execution occurs.</p>
     */
    @Override
    public void execute() {
        System.out.println("🔚 End node reached. No further action.");
    }

    /**
     * {@inheritDoc}
     * Prevents unsupported operations on end nodes.
     *
     * @param operation The requested operation (ignored).
     * @throws UnsupportedOperationForNodeException Always thrown, as EndNode is passive.
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
