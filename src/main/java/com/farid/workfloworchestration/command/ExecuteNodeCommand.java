package com.farid.workfloworchestration.command;

import com.farid.workfloworchestration.model.WorkflowNode;
import com.farid.workfloworchestration.model.ExecutableNode;

/**
 * Command that executes a node in the workflow, but only if the node
 * implements actual execution logic (i.e., is an ExecutableNode).
 *
 * <p>This class uses polymorphism and safe type-checking to allow
 * only nodes with business logic to be executed at runtime.</p>
 *
 * It adheres to the Command design pattern, allowing execution
 * behavior to be encapsulated as an object.
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */

public class ExecuteNodeCommand implements WorkflowCommand { // Subtyping Polymorphism

    // === Information Hiding ===
// node is private and immutable; no external class can access or modify it.
    private final WorkflowNode node;


    /**
     * Constructs an ExecuteNodeCommand for the given node.
     * Demonstrates Dependency Injection by passing the node in the constructor.
     *
     * @param node The workflow node to be executed (if applicable)
     */
    public ExecuteNodeCommand(WorkflowNode node) {
        this.node = node; // Encapsulation
    }

    /**
     * Executes the node's internal logic if it is an executable node.
     * Uses safe runtime polymorphism (via instanceof and casting).
     */
    @Override
    public void execute() {
        // Coercion Polymorphism: Casts to ExecutableNode only after runtime check
        // Subtyping Polymorphism: ExecutableNode is a subtype of WorkflowNode
        if (node instanceof ExecutableNode executableNode) {
            executableNode.execute(); // Dynamic dispatch — executes the actual subclass logic
        } else {
            // Information Hiding: caller doesn’t need to know how execution fails
            System.out.println("Node '" + node.getName() + "' is not executable. NodeType: " + node.getNodeType());
        }
    }
}
