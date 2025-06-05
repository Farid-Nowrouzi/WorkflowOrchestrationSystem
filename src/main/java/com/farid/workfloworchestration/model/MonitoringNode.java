package com.farid.workfloworchestration.model;

import com.farid.workfloworchestration.exception.UnsupportedOperationForNodeException;

import java.util.Map;

/**
 * {@code MonitoringNode} is responsible for collecting runtime metrics, such as
 * system health, throughput, or execution logs during a machine learning pipeline.
 *
 * <p><strong>OOP Principles Demonstrated:</strong></p>
 * <ul>
 *   <li><b>Inheritance:</b> Extends {@link ExecutableNode} to gain execution behavior</li>
 *   <li><b>Polymorphism:</b>
 *     <ul>
 *       <li>Runtime: Overrides {@code execute()} and {@code executeWithContext()}</li>
 *       <li>Parametric: Uses {@code <String>} generic logging for context-flexibility</li>
 *     </ul>
 *   </li>
 *   <li><b>Encapsulation:</b> Monitoring logic and context execution are internally managed</li>
 *   <li><b>Abstraction:</b> Implements abstract methods from its superclass</li>
 * </ul>
 */
public class MonitoringNode extends ExecutableNode<String> {

    /**
     * Constructor for basic monitoring node with default description.
     *
     * @param id   Unique identifier for the node
     * @param name Human-readable node name
     */
    public MonitoringNode(String id, String name) {
        super(id, name, NodeType.MONITORING);
    }

    /**
     * Constructor for monitoring node with description support.
     *
     * @param id          Node identifier
     * @param name        Node name
     * @param description Textual description of monitoring purpose
     */
    public MonitoringNode(String id, String name, String description) {
        super(id, name, description, NodeType.MONITORING);
    }

    /**
     * Default execution logic. This is called when no external context is provided.
     */
    @Override
    public void execute() {
        System.out.println("📈 Monitoring in progress for: " + getName());
        System.out.println("✅ System status and metrics are being collected...");
    }

    /**
     * Validates whether the node is properly configured.
     * Always returns true unless extended with validation logic.
     */
    @Override
    public boolean isValid() {
        return true; // Add validation for system hooks or APIs if needed
    }

    /**
     * Executes the monitoring logic while using runtime metadata (context).
     *
     * @param context Key-value map representing metadata or system state
     */
    @Override
    public void executeWithContext(Map<String, String> context) {
        System.out.println("🛠 Monitoring with context: " + context);
        executionLogger.log("MonitoringNode executed with context: " + context);
    }

    /**
     * Prevents unsupported operations (e.g., transformation or model selection logic).
     *
     * @param operation Name of attempted operation
     * @throws UnsupportedOperationForNodeException Always, since operations are disallowed
     */
    @Override
    public void validateOperation(String operation) throws UnsupportedOperationForNodeException {
        throw new UnsupportedOperationForNodeException(
                "Operation '" + operation + "' is not supported by node: " + getName());
    }

}
