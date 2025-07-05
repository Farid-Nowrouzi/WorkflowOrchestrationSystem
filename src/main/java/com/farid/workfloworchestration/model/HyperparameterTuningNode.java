package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;
import com.farid.workfloworchestration.util.MetadataPrinter;

import java.util.Map;

/**
 * {@code HyperparameterTuningNode} is a specialized executable node in the workflow
 * responsible for optimizing model parameters (e.g., learning rate, batch size, epochs)
 * through grid search, random search, or Bayesian methods.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Inherits from {@link ExecutableNode} to gain common execution structure</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li><i>Runtime:</i> Overrides abstract methods for custom execution</li>
 *       <li><i>Parametric:</i> Uses {@code <String>} for flexible log handling</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> Internal tuning logic and logging are abstracted from external access</li>
 *   <li><b>Abstraction:</b> Defines abstract execution logic using context input</li>
 *   <li><b>Aggregation:</b> Reuses {@link MetadataPrinter} for metadata formatting without owning it</li>
 * </ul>
 *
 * <p>This node supports integration into ML pipelines requiring dynamic tuning logic.</p>
 */
public class HyperparameterTuningNode extends ExecutableNode<String> {

    // === Aggregation: Utility injected for metadata formatting ===
    private final MetadataPrinter<Map<String, String>> metadataPrinter = new MetadataPrinter<>();

    /**
     * Constructor for basic instantiation without description.
     *
     * @param id   Unique node ID
     * @param name Display name for the node
     */
    public HyperparameterTuningNode(String id, String name) {
        super(id, name, NodeType.HYPERPARAMETER_TUNING);
    }

    /**
     * Constructor with a description for detailed metadata.
     *
     * @param id          Node identifier
     * @param name        Display name
     * @param description Human-readable summary of the tuning operation
     */
    public HyperparameterTuningNode(String id, String name, String description) {
        super(id, name, description, NodeType.HYPERPARAMETER_TUNING);
    }

    /**
     * Executes the node logic in a default context.
     * Simulates hyperparameter tuning (e.g., grid search, random search).
     */
    @Override
    public void execute() {
        System.out.println(" Hyperparameter Tuning node executed: searching for optimal parameters...");
    }

    /**
     * Validates if the node is correctly set up.
     * Always returns true for now but can be extended (e.g., check if search space is defined).
     *
     * @return true if valid
     */
    @Override
    public boolean isValid() {
        return true;
    }

    /**
     * Executes the node with context-aware input (e.g., ranges, search strategy).
     *
     * @param context Runtime metadata for tuning logic
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println(" Hyperparameter Tuning node executed with context: " + context);

        // === Demonstrate printMetadata() with all overloads ===
        System.out.println(" [Step 1] No-arg printMetadata():");
        metadataPrinter.printMetadata();  // ✔ No metadata provided

        System.out.println(" [Step 2] printMetadata(context, prefix):");
        metadataPrinter.printMetadata(context, "[TUNING-META]"); //  Already in use

        System.out.println(" [Step 3] printMetadata(context, prefix, keyContains = 'param'):");
        metadataPrinter.printMetadata(context, "[FILTERED]", "param"); //  Filters keys containing "param"

        // === Log execution ===
        executionLogger.log("Hyperparameter Tuning executed with context: " + context);
    }


    /**
     * Validates if a specific operation is supported by this node.
     *
     * @param operation Operation name to validate
     * @throws UnsupportedOperationForNodeException always, as this node doesn't support external ops
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName()
        );
    }
}
