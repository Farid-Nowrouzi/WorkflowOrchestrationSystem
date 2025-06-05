package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.InvalidMetadataException;

import java.util.Map;

/**
 * {@code InferenceNode} represents a model inference component in the workflow.
 * It uses runtime metadata to simulate prediction generation based on a trained model.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link ExecutableNode} to share execution logic</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li><i>Runtime:</i> Overrides execution behavior</li>
 *       <li><i>Parametric:</i> Uses generic logging via {@code <String>}</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> Inference and validation logic is hidden inside the class</li>
 *   <li><b>Exception Handling:</b> Validates metadata and throws {@link InvalidMetadataException}</li>
 * </ul>
 *
 * <p>Typical use: Triggering predictions using metadata keys like <i>modelName</i> and <i>inputData</i>.</p>
 */
public class InferenceNode extends ExecutableNode<String> {

    /**
     * Basic constructor without description.
     * @param id   Unique node ID
     * @param name Human-readable node name
     */
    public InferenceNode(String id, String name) {
        super(id, name, NodeType.INFERENCE);
    }

    /**
     * Constructor with additional node description.
     * @param id          Unique node ID
     * @param name        Node name
     * @param description Textual description of the inference process
     */
    public InferenceNode(String id, String name, String description) {
        super(id, name, description, NodeType.INFERENCE);
    }

    /**
     * Default execution fallback if no metadata is provided.
     */
    @Override
    public void execute() {
        System.out.println("🔮 Inference executed for: " + getName());
        System.out.println("📊 Generating predictions from trained model...");
    }

    /**
     * Validates if this node is logically configured (e.g., model/data checks can be added).
     * @return true by default
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes inference using runtime metadata such as model name and input.
     *
     * @param context Map containing keys like "modelName" and "inputData"
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🔍 Inference with context: " + context);

        try {
            // Extract metadata
            String modelName = context.get("modelName");
            String inputData = context.get("inputData");

            // Validate metadata
            if (modelName == null || modelName.isBlank()) {
                throw new InvalidMetadataException("Missing or empty modelName in metadata.");
            }

            if (inputData == null || inputData.isBlank()) {
                throw new InvalidMetadataException("Missing or empty inputData in metadata.");
            }

            // Perform inference
            System.out.println("✅ Using model: " + modelName);
            System.out.println("✅ Input data: " + inputData);

            String result = "Inference executed using model: " + modelName + " with input: " + inputData;
            executionLogger.logWithTag(result, "INFERENCE");

        } catch (InvalidMetadataException e) {
            System.err.println("❌ Metadata error during inference: " + e.getMessage());
            executionLogger.logWithTag("❌ Metadata error: " + e.getMessage(), "INFERENCE");
        }
    }

    /**
     * Validates whether a given operation is supported.
     * In this case, inference nodes support all operations by default (for demonstration).
     *
     * @param operation The operation string
     */
    @Override
    public void validateOperation(String operation) {
        System.out.println("✅ Operation '" + operation + "' is supported by InferenceNode: " + getName());
    }
}
