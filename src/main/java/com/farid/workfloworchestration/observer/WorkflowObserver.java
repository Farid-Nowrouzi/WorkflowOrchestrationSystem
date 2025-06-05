package com.farid.workfloworchestration.observer;

import com.farid.workfloworchestration.model.WorkflowNode;

/**
 * Defines the contract for observers that want to listen to workflow node events.
 *
 * <p>This interface is a core part of the Observer design pattern, allowing different
 * parts of the system (e.g., UI, logger, validator) to respond to node-related actions
 * without being tightly coupled to the node execution logic.</p>
 *
 * <p><strong>OOP Principles Applied:</strong></p>
 * <ul>
 *   <li><b>Abstraction:</b> Only declares behavior, not implementation.</li>
 *   <li><b>Polymorphism:</b> Different observers may react differently to the same event.</li>
 *   <li><b>Loose Coupling:</b> Observers can be independently added or removed.</li>
 * </ul>
 */
public interface WorkflowObserver {

    /**
     * Callback method triggered when an event occurs on a workflow node.
     *
     * @param node    the {@link WorkflowNode} that generated the event
     * @param message a descriptive message (e.g., "Node Created", "Connected to Node X")
     */
    void onNodeEvent(WorkflowNode node, String message);
}
