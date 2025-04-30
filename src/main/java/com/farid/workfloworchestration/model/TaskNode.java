package com.farid.workfloworchestration.model;

/**
 * Demonstrates: Inheritance, Polymorphism (Overriding), SRP (Single Responsibility)
 */
public class TaskNode extends WorkflowNode {

    private String taskDetails;

    public TaskNode(String id, String name) {
        super(id, name, NodeType.TASK);
        this.taskDetails = "Default Task Details";
    }

    public TaskNode(String id, String name, String taskDetails) {
        super(id, name, NodeType.TASK);
        this.taskDetails = taskDetails;
    }

    public TaskNode(String id, String name, String description, String taskDetails) {
        super(id, name, description, NodeType.TASK);
        this.taskDetails = taskDetails;
    }

    public String getTaskDetails() {
        return taskDetails;
    }

    public void setTaskDetails(String taskDetails) {
        this.taskDetails = taskDetails;
    }

    @Override
    public void execute() {
        System.out.println("Executing task: " + getName() + " -> " + taskDetails);
    }

    @Override
    public boolean isValid() {
        return taskDetails != null && !taskDetails.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "[TASK] " + getName() + " (Details: " + taskDetails + ")";
    }
}

