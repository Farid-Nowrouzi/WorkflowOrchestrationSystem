package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;
import com.farid.workfloworchestration.util.MetadataPrinter;

import java.util.Map;

/**
 * Represents a node responsible for executing an analysis step in the workflow.
 *
 * <p>This node is a concrete subclass of {@link WorkflowNode} and encapsulates
 * logic specific to analysis tasks (e.g., statistical processing).</p>
 *
 * <p>It demonstrates object-oriented principles such as inheritance,
 * polymorphism, abstraction, and encapsulation. It also implements custom
 * validation logic and enforces operation restrictions via exception handling.</p>
 *
 * Example behavior:
 * <pre>
 *     AnalysisNode node = new AnalysisNode("id1", "Stat Analysis", "Statistical");
 *     node.execute(); // prints "Running analysis: Statistical"
 * </pre>
 *
 * @author Farid Nowrouzi
 * @version 1.0
 */
public class AnalysisNode extends WorkflowNode { // Inheritance

    // === Private Field (Information Hiding Compliance) ===
    private String analysisType;

    // === Metadata Printer (Aggregation) ===
    private final MetadataPrinter<Map<String, String>> metadataPrinter = new MetadataPrinter<>();

    /**
     * Constructs an AnalysisNode with the given ID, name, and analysis type.
     *
     * @param id           Unique identifier
     * @param name         Display name
     * @param analysisType Type of analysis performed by this node
     */
    public AnalysisNode(String id, String name, String analysisType) {
        super(id, name, NodeType.ANALYSIS); // Inheritance, Constructor Chaining
        this.analysisType = analysisType;
    }

    /**
     * Executes the analysis step by printing the type.
     *
     * Demonstrates polymorphic behavior via method overriding.
     */
    @Override
    public void execute() { // Subtyping Polymorphism
        System.out.println("Running analysis: " + analysisType);

        // === Demonstrate MetadataPrinter usage (Aggregation + Utility) ===
        System.out.println(" Executing with metadata:");
        metadataPrinter.printMetadata(getMetadata()); // General print
        metadataPrinter.printMetadata(getMetadata(), "[ANALYSIS]", "type"); // Prefixed + filtered
    }

    /**
     * Validates the internal state of the node.
     *
     * @return true if analysis type is non-null and not blank
     */
    @Override
    public boolean isValid() {
        return analysisType != null && !analysisType.isBlank();
    }

    /**
     * Throws an exception if any operation is attempted, as this node doesn't support external ops.
     *
     * @param operation The operation name being validated
     * @throws UnsupportedOperationForNodeException always
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException("Operation '" + operation + "' is not supported by node: " + getName());
    }
}
