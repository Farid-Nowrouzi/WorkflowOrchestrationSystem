package com.farid.workfloworchestration.command;

/**
 * Represents a generic command in the workflow orchestration system.
 *
 * <p>This interface defines a common contract for any command that can be
 * executed in the system, including actions like connecting nodes, deleting nodes,
 * executing them, etc.</p>
 *
 * <p>This is the base component of the Command design pattern, enabling
 * encapsulation of actions as first-class objects.</p>
 *
 * <p>It supports modularity, extensibility, and polymorphism by allowing
 * different command implementations to be executed uniformly.</p>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
// === Abstraction Interface ===
// No internal state to hide — information hiding not applicable here.

public interface WorkflowCommand { // Interface-Based Abstraction

    /**
     * Executes the command logic.
     * Each implementing class defines its specific behavior here.
     */
    void execute(); // Abstraction, Polymorphism
}
