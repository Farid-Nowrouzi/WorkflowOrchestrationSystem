package com.farid.workfloworchestration.command;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Acts as the invoker in the Command design pattern.
 *
 * <p>Maintains a queue of workflow commands and executes them in order.
 * This class is responsible for decoupling the command execution from
 * command definition.</p>
 *
 * <p>Allows commands to be added, run in sequence, and cleared when needed.</p>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class WorkflowInvoker {

    // Encapsulation: The command queue is private and cannot be accessed externally
    private final Queue<WorkflowCommand> commandQueue = new LinkedList<>(); // Composition (has-a Queue of Commands)

    /**
     * Adds a command to the internal queue for later execution.
     *
     * @param command A command implementing WorkflowCommand
     */
    public void addCommand(WorkflowCommand command) {
        commandQueue.add(command); // Abstraction: interacts only with interface type
    }

    /**
     * Executes all queued commands in FIFO order.
     * Uses polymorphism to call execute() without knowing specific implementations.
     */
    public void runAll() {
        while (!commandQueue.isEmpty()) {
            WorkflowCommand command = commandQueue.poll();
            command.execute(); // Subtyping Polymorphism: dynamic method dispatch
        }
    }

    /**
     * Clears the command queue.
     * Ensures the invoker no longer holds references to previously added commands.
     */
    public void clear() {
        commandQueue.clear();
    }
}
