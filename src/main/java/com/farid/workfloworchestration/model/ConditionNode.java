package com.farid.workfloworchestration.model;

// Demonstrates: Inheritance, Overriding, Polymorphism
public class ConditionNode extends WorkflowNode {

    private String conditionExpression; // e.g., "x > 10"

    public ConditionNode(String id, String name) {
        super(id, name, NodeType.CONDITION);
        this.conditionExpression = "";
    }

    public ConditionNode(String id, String name, String description, String conditionExpression) {
        super(id, name, description, NodeType.CONDITION);
        this.conditionExpression = conditionExpression;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    @Override
    public void execute() {
        System.out.println("Evaluating condition: " + getName() + " -> " + conditionExpression);
        boolean result = Math.random() > 0.5;
        System.out.println("Result: " + result);
    }

    @Override
    public boolean isValid() {
        return conditionExpression != null && !conditionExpression.trim().isEmpty();
    }
}
