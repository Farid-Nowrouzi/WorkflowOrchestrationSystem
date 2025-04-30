package com.farid.workfloworchestration.observer;

import com.farid.workfloworchestration.model.WorkflowNode;

/**
 * Interface for observing workflow node events.
 *
 * <p>Use this interface to receive notifications when something important
 * happens in the workflow (e.g., node created, connected, executed).</p>
 *
 * OOP Principles:
 * - Abstraction: Only defines the contract.
 * - Polymorphism: Multiple observers can act differently.
 * - Loose Coupling: Observers can be added/removed freely.
 */
public interface WorkflowObserver {

    /**
     * Called when an event occurs on a WorkflowNode.
     *
     * @param node    the node that triggered the event
     * @param message a descriptive message (e.g., "Node Created", "Connected to X")
     */
    void onNodeEvent(WorkflowNode node, String message);
}
