package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;
import com.farid.workfloworchestration.util.MetadataPrinter;

import java.util.Map;

/**
 * TaskNode
 *
 * Represents a general-purpose task node in the workflow.
 *
 * <p><strong>OOP Concepts Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link ExecutableNode}</li>
 *   <li><b>Polymorphism:</b> Overrides multiple methods from superclass</li>
 *   <li><b>Parametric Polymorphism:</b> Uses {@code MetadataPrinter<T>}</li>
 *   <li><b>Encapsulation:</b> Encapsulates task-specific behavior</li>
 *   <li><b>SRP (Single Responsibility Principle):</b> Focused on task logic only</li>
 * </ul>
 */
public class TaskNode extends ExecutableNode<String> {

    // Describes what the task does (customizable per instance)
    private String taskDetails;

    /**
     * Constructs a TaskNode with default task details.
     */
    public TaskNode(String id, String name) {
        super(id, name, NodeType.TASK);
        this.taskDetails = "Default Task Details";
    }

    /**
     * Constructs a TaskNode with a specific task description.
     */
    public TaskNode(String id, String name, String taskDetails) {
        super(id, name, NodeType.TASK);
        this.taskDetails = taskDetails;
    }

    /**
     * Constructs a TaskNode with name, description, and task details.
     */
    public TaskNode(String id, String name, String description, String taskDetails) {
        super(id, name, description, NodeType.TASK);
        this.taskDetails = taskDetails;
    }

    // Getter and setter for task details
    public String getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    /**
     * Checks if the task details are valid (non-empty).
     */
    @Override
    public boolean isValid() {
        return taskDetails != null && !taskDetails.trim().isEmpty();
    }

    /**
     * String representation of the node for UI or debug output.
     */
    @Override
    public String toString() {
        return "[TASK] " + getName() + " (Details: " + taskDetails + ")";
    }

    /**
     * Executes the task without additional context.
     */
    @Override
    public void execute() {
        System.out.println("Executing task: " + getName() + " -> " + taskDetails);

        // ✅ Demonstrates use of parametric polymorphism (Generics)
        MetadataPrinter<Map<String, String>> printer = new MetadataPrinter<>();
        printer.printMetadata(getMetadata());
    }

    /**
     * Executes the task using contextual metadata.
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("Executing task with context: " + context + " for node: " + getName());
        executionLogger.log("TaskNode executed with context: " + context);
    }

    /**
     * Rejects unsupported operations for this node type.
     *
     * @param operation Label of the attempted operation
     * @throws UnsupportedOperationForNodeException Always thrown for unsupported ops
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
