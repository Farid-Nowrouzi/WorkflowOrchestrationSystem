package com.farid.workfloworchestration.model;

// Demonstrates: Inheritance, Overriding, Polymorphism
public class PredictionNode extends WorkflowNode {

    private String modelName; // Name of the ML model to use

    // Constructor with only id and name (Default modelName)
    public PredictionNode(String id, String name) {
        super(id, name, NodeType.PREDICTION);
        this.modelName = ""; // Default empty model name
    }

    // Constructor with id, name, description, and modelName
    public PredictionNode(String id, String name, String description, String modelName) {
        super(id, name, description, NodeType.PREDICTION);
        this.modelName = modelName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    @Override
    public void execute() {
        System.out.println("Running prediction using model: " + modelName + " for node: " + getName());
    }

    @Override
    public boolean isValid() {
        return modelName != null && !modelName.trim().isEmpty();
    }
}
